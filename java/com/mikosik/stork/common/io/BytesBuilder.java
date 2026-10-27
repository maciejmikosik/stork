package com.mikosik.stork.common.io;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;

public class BytesBuilder {
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  public static BytesBuilder bytesBuilder() {
    return new BytesBuilder();
  }

  public BytesBuilder appendAscii(String string) {
    output.writeBytes(string.getBytes(US_ASCII));
    return this;
  }

  public BytesBuilder append(byte oneByte) {
    output.write(oneByte);
    return this;
  }

  public byte[] build() {
    return output.toByteArray();
  }
}
