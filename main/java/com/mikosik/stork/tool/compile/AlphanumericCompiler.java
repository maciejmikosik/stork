package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.Throwables.fail;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;

import com.mikosik.stork.common.PeekingInput;

public class AlphanumericCompiler implements Compiler<String> {
  public String compile(PeekingInput input) {
    return isAlphanumeric(input.peek())
        ? string(parseAlphanumeric(input))
        : fail("expected alphanumeric but was %c", input.peek());
  }

  private byte[] parseAlphanumeric(PeekingInput input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (isAlphanumeric(input.peek())) {
      buffer.write(input.read());
    }
    return buffer.toByteArray();
  }

  private static String string(byte[] buffer) {
    return new String(buffer, US_ASCII);
  }
}
