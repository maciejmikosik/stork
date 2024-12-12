package com.mikosik.stork.common;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;

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
    var charset = UTF_8;
    var buffer = newBuffer();
    throwable.printStackTrace(buffer.asOutput().asPrintStream(charset));
    return new String(buffer.bytes(), charset);
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }
}
