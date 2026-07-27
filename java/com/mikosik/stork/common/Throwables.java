package com.mikosik.stork.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
  public static RuntimeException runtimeException(String message) {
    return new RuntimeException(message);
  }

  public static RuntimeException runtimeException(String key, Object value) {
    return new RuntimeException(key + ": " + value);
  }

  public static LinkageError linkageError(Throwable cause) {
    return new LinkageError("", cause);
  }

  public static String messageOf(Throwable exception) {
    var message = exception.getMessage();
    return message == null ? "" : message;
  }

  public static String stackTraceOf(Throwable throwable) {
    var buffer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(buffer));
    return buffer.toString();
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }
}
