package com.mikosik.stork.build.compile;

public class ByteToken implements Token {
  public final Byte value;

  private ByteToken(Byte value) {
    this.value = value;
  }

  public static ByteToken token(Byte value) {
    return new ByteToken(value);
  }
}
