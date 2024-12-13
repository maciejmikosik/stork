package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Output implements AutoCloseable {
  private final OutputStream output;

  private Output(OutputStream output) {
    this.output = output;
  }

  public static Output output(OutputStream output) {
    return new Output(output);
  }

  public static Output noOutput() {
    return output(OutputStream.nullOutputStream());
  }

  public static Output output(Path file) {
    try {
      return output(Files.newOutputStream(file));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void write(byte b) {
    try {
      output.write(b);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void write(byte[] bytes) {
    try {
      output.write(bytes);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void flush() {
    try {
      output.flush();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void close() {
    try {
      output.close();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }
}
