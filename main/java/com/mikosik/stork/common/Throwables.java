package com.mikosik.stork.common;

public class Throwables {
  public static <T> T throwing(RuntimeException exception) {
    throw exception;
  }

  public static <T> T fail(String message) {
    throw new RuntimeException(message);
  }
}
