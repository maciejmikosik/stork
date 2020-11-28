package com.mikosik.stork.core;

import static com.mikosik.stork.common.InputOutput.buffered;
import static com.mikosik.stork.tool.compile.Compiler.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.mikosik.stork.data.model.Module;

public class Repository {
  private Repository() {}

  public static Repository repository() {
    return new Repository();
  }

  public Module module(String fileName) {
    try (InputStream input = buffered(resource(fileName))) {
      return compiler().compile(input);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static InputStream resource(String fileName) {
    return Repository.class.getResourceAsStream(fileName);
  }
}
