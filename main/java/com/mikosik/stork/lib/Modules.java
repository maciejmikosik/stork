package com.mikosik.stork.lib;

import static com.mikosik.stork.tool.Default.compileModule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.mikosik.stork.data.model.Module;

public class Modules {
  public static Module module(String name) {
    return compileModule(readFile(name));
  }

  private static String readFile(String name) {
    Class<?> type = Modules.class;
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
