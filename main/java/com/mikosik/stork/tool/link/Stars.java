package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.common.InputOutput.walk;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;
import static com.mikosik.stork.tool.link.Link.link;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isRegularFile;
import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;
import com.mikosik.stork.tool.compile.Compiler;

public class Stars {
  public static Module moduleFromDirectory(Path directory) {
    List<Module> modules = walk(directory)
        .filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().equals("stork"))
        .map(file -> moduleFromFile(directory, file))
        .collect(toList());
    return link(chainFrom(modules));
  }

  private static Module moduleFromFile(Path directory, Path file) {
    String packagePrefix = packagePrefix(directory, file);
    Chain<String> imports = importsFor(file);
    Compiler<Module> compiler = defaultCompiler();
    try (Input input = input(file).buffered()) {
      return import_(imports, export(packagePrefix, compiler.compile(input)));
    }
  }

  private static Chain<String> importsFor(Path file) {
    Path importFile = file.getParent().resolve("import");
    Chain<String> result = empty();
    if (isRegularFile(importFile)) {
      try (Scanner scanner = input(importFile).buffered().scan(UTF_8)) {
        while (scanner.hasNext()) {
          result = result.add(scanner.nextLine());
        }
      }
    }
    return result;
  }

  private static String packagePrefix(Path directory, Path file) {
    Path path = directory.relativize(file.getParent());
    String packageName = path.toString().replace('/', '.');
    return packageName.isEmpty()
        ? packageName
        : packageName + '.';
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

  private static Module import_(Chain<String> imports, Module module) {
    for (String import_ : imports) {
      Variable global = variable(import_);
      Variable local = variable(LOCAL.format(global));
      module = renameTo(global, local, module);

    }
    return module;
  }

  private static Module renameTo(
      Variable replacement,
      Variable original,
      Module module) {
    return new Traverser() {
      protected Variable traverse(Variable variable) {
        return variable.name.equals(original.name)
            ? replacement
            : variable;
      }

      protected Variable traverseDefinitionName(Variable variable) {
        return traverse(variable);
      }
    }.traverse(module);
  }
}
