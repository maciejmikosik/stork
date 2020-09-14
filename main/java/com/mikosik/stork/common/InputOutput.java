package com.mikosik.stork.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public class InputOutput {
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
}
