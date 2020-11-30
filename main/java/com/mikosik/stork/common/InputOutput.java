package com.mikosik.stork.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class InputOutput {
  public static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }

  public static Stream<Path> list(Path directory) {
    try {
      return Files.list(directory);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
