package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;
import static com.mikosik.stork.tool.link.Build.build;
import static com.mikosik.stork.tool.link.Link.link;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compile.Compiler;

public class Stars {
  public static Module moduleFromDirectory(Path directory) {
    try {
      List<Module> modules = walk(directory)
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().equals("stork"))
          .map(Stars::moduleFromFile)
          .collect(toList());
      return link(chainFrom(modules));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Module moduleFromFile(Path file) {
    Compiler<Module> compiler = defaultCompiler();
    try (Input input = input(file).buffered()) {
      return build(compiler.compile(input));
    }
  }
}
