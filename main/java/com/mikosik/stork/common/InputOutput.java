package com.mikosik.stork.common;

import static java.nio.file.Files.exists;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class InputOutput {
  public static InputStream input(byte[] bytes) {
    return new ByteArrayInputStream(bytes);
  }

  public static void pump(InputStream input, OutputStream output) {
    try {
      int oneByte;
      while ((oneByte = input.read()) != -1) {
        output.write(oneByte);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static byte[] readAllBytes(InputStream input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    pump(input, buffer);
    return buffer.toByteArray();
  }

  public static byte[] readAllBytes(Path file) {
    try {
      return Files.readAllBytes(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static byte[] tryReadAllBytes(Path file) {
    return exists(file)
        ? readAllBytes(file)
        : new byte[0];
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

  public static InputStream buffered(InputStream input) {
    return new BufferedInputStream(input);
  }

  public static String readResource(Class<?> type, String name) {
    try (InputStream input = buffered(type.getResourceAsStream(name))) {
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
