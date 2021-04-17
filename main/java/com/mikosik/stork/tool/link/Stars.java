package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;
import static com.mikosik.stork.tool.link.Build.build;
import static com.mikosik.stork.tool.link.Build.renameTo;
import static com.mikosik.stork.tool.link.Link.link;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.compile.Compiler;

public class Stars {
  public static Module moduleFromDirectory(Path directory) {
    try {
      List<Module> modules = walk(directory)
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().equals("stork"))
          .map(file -> moduleFromFile(directory, file))
          .collect(toList());
      return link(chainFrom(modules));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Module moduleFromFile(Path directory, Path file) {
    String packagePrefix = packagePrefix(directory, file);
    Compiler<Module> compiler = defaultCompiler();
    try (Input input = input(file).buffered()) {
      return export(packagePrefix, build(compiler.compile(input)));
    }
  }

  private static String packagePrefix(Path directory, Path file) {
    Path path = directory.relativize(file.getParent());
    return path.toString().replace('/', '.') + '.';
  }

  private static Module export(String packagePrefix, Module module) {
    Chain<Definition> definitions = module.definitions;
    for (Definition definition : definitions) {
      Variable original = definition.variable;
      Variable replacement = variable(packagePrefix + original.name);
      module = renameTo(replacement, original, module);
    }
    return module;
  }
}
