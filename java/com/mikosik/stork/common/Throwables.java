package com.mikosik.stork.common;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

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

  public static String messageOf(Throwable exception) {
    String message = exception.getMessage();
    return message == null ? "" : message;
  }

  public static String stackTraceOf(Throwable throwable) {
    var charset = UTF_8;
    var buffer = newBuffer();
    throwable.printStackTrace(buffer.asOutput().asPrintStream(charset));
    return new String(buffer.bytes(), charset);
  }

}
