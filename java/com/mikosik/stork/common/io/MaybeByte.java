package com.mikosik.stork.common.io;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.format;

public class MaybeByte {
  private static final MaybeByte[] BYTES = new MaybeByte[257];

  static {
    BYTES[0] = new MaybeByte((byte) 0);
    for (int i = 0; i < 256; i++) {
      BYTES[i + 1] = new MaybeByte((byte) i);
    }
  }

  private final byte value;

  private MaybeByte(byte value) {
    this.value = value;
  }

  public static MaybeByte justByte(byte value) {
    return BYTES[toUnsignedInt(value) + 1];
  }

  public static MaybeByte noByte() {
    return BYTES[0];
  }

  public static MaybeByte maybeByte(int code) {
    return BYTES[code + 1];
  }

  public boolean hasByte() {
    return this != BYTES[0];
  }

  public byte getByte() {
    if (this == BYTES[0]) {
      throw new RuntimeException("NO_BYTE");
    } else {
      return value;
    }
  }

  public String toString() {
    return this == BYTES[0]
        ? "NO_BYTE"
        : format("just(%d)", toUnsignedInt(value));
  }
}
