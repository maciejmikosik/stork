package com.mikosik.stork.lib;

import static com.mikosik.stork.lib.CoreLibrary.coreLibrary;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import com.mikosik.stork.common.Stream;
import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.tool.Compiler;
import com.mikosik.stork.tool.Parser;

public class Libraries {
  public static Library library(String name) {
    return name.equals("core.stork")
        ? coreLibrary()
        : fromFile(name);
  }

  private static Library fromFile(String name) {
    List<Definition> definitions = stream(readFile(name).split("\n\n"))
        .map(String::trim)
        .filter(source -> source.length() > 0)
        .map(Stream::stream)
        .map(Parser::parse)
        .map(Compiler::compileDefinition)
        .collect(toList());

    return Library.library(definitions);
  }

  private static String readFile(String name) {
    Class<?> type = Libraries.class;
    try (InputStream input = new BufferedInputStream(type.getResourceAsStream(name))) {
      StringBuilder builder = new StringBuilder();
      while (input.available() > 0) {
        builder.append((char) input.read());
      }
      return builder.toString();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
