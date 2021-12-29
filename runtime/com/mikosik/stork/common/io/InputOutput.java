package com.mikosik.stork.common.io;

import java.io.IOException;
import java.io.UncheckedIOException;

public class InputOutput {
  public static UncheckedIOException unchecked(IOException e) {
    return new UncheckedIOException(e);
  }
}
