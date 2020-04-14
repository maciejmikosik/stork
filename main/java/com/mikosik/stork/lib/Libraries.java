package com.mikosik.stork.lib;

import static com.mikosik.stork.tool.Default.compileLibrary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.mikosik.stork.data.model.Library;

public class Libraries {
  public static Library library(String name) {
    return compileLibrary(readFile(name));
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
