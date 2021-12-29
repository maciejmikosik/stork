package com.mikosik.stork.common.io;

import static java.lang.String.format;

public class MaybeByte {
  private static final MaybeByte NO_BYTE = new MaybeByte((byte) 0);
  private static final MaybeByte[] BYTES = new MaybeByte[256];

  static {
    for (int i = 0; i < BYTES.length; i++) {
      BYTES[i] = new MaybeByte((byte) i);
    }
  }

  private final byte value;

  private MaybeByte(byte value) {
    this.value = value;
  }

  public static MaybeByte justByte(byte value) {
    return BYTES[unsigned(value)];
  }

  public static MaybeByte noByte(byte value) {
    return NO_BYTE;
  }

  public static MaybeByte maybeByte(int code) {
    return code == -1 ? NO_BYTE : justByte((byte) code);
  }

  public boolean hasByte() {
    return this != NO_BYTE;
  }

  public byte getByte() {
    if (this == NO_BYTE) {
      throw new RuntimeException("NO_BYTE");
    } else {
      return value;
    }
  }

  public String toString() {
    return this == NO_BYTE
        ? "NO_BYTE"
        : format("just(%d)", unsigned(value));
  }

  private static int unsigned(byte value) {
    return value & 0xFF;
  }
}
