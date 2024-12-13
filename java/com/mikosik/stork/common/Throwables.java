package com.mikosik.stork.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
  public static RuntimeException runtimeException(String format, Object... args) {
    return new RuntimeException(format.formatted(args));
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
