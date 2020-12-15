package com.mikosik.stork.common;

import static java.lang.String.format;

public class Throwables {
  public static <T> T throwing(RuntimeException exception) {
    throw exception;
  }

  public static <T> T fail(String message) {
    throw new RuntimeException(message);
  }

  public static <T> T fail(String format, Object... args) {
    throw new RuntimeException(format(format, args));
  }
}
