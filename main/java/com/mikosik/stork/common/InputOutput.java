package com.mikosik.stork.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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

  public static PrintStream printStream(OutputStream output, Charset charset) {
    try {
      return new PrintStream(output, false, charset.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
