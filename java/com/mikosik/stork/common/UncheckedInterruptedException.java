package com.mikosik.stork.common;

public class UncheckedInterruptedException extends RuntimeException {
  public UncheckedInterruptedException() {}

  public UncheckedInterruptedException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(
        message,
        cause,
        enableSuppression,
        writableStackTrace);
  }

  public UncheckedInterruptedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UncheckedInterruptedException(String message) {
    super(message);
  }

  public UncheckedInterruptedException(Throwable cause) {
    super(cause);
  }

  public static UncheckedInterruptedException uncheckedInterruptedException() {
    return new UncheckedInterruptedException();
  }

  public static UncheckedInterruptedException unchecked(InterruptedException e) {
    return new UncheckedInterruptedException(e);
  }
}
