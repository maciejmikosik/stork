package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.Collections.stream;
import static com.mikosik.stork.common.Sequence.toSequence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.mikosik.stork.common.Sequence;

public class InputOutput {
  public static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }

  public static Path path(String name) {
    return Paths.get(name);
  }

  public static Stream<Path> children(Path path) {
    try {
      return Files.list(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Stream<Path> walk(Path path) {
    try {
      return Files.walk(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static void delete(Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static void deleteRecursively(Path path) {
    if (Files.isDirectory(path)) {
      children(path).forEach(InputOutput::deleteRecursively);
    }
    delete(path);
  }

  public static Path createTempDirectory(String prefix) {
    try {
      return Files.createTempDirectory(prefix);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Path createFile(Path path) {
    try {
      return Files.createFile(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Path createDirectories(Path path) {
    try {
      return Files.createDirectories(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Sequence<String> components(Path path) {
    return stream(path)
        .map(Path::toString)
        .collect(toSequence());
  }
}
