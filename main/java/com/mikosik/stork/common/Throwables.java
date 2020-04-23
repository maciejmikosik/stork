package com.mikosik.stork.common;

public class Throwables {
  public static <T> T throwing(RuntimeException exception) {
    throw exception;
  }
}
