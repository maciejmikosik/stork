package com.mikosik.stork.common;

public class Check {
  public static void check(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }
}
