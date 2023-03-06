package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class InputOutput {
  public static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }

  public static String formatStackTrace(Throwable throwable) {
    var charset = UTF_8;
    var buffer = newBuffer();
    throwable.printStackTrace(buffer.asOutput().asPrintStream(charset));
    return new String(buffer.bytes(), charset);
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
}
