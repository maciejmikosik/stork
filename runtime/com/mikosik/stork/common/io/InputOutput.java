package com.mikosik.stork.common.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class InputOutput {
  public static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }

  public static Path path(String path) {
    return Paths.get(path);
  }

  public static Stream<Path> list(Path directory) {
    try {
      return Files.list(directory);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Stream<Path> walk(Path directory) {
    try {
      return Files.walk(directory);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }
}
