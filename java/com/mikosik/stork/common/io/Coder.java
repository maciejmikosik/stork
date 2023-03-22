package com.mikosik.stork.common.io;

import java.nio.charset.Charset;

public interface Coder {
  byte[] encode(String string);

  String decode(byte[] bytes);

  public static Coder coder(Charset charset) {
    return new Coder() {
      public byte[] encode(String string) {
        return string.getBytes(charset);
      }

      public String decode(byte[] bytes) {
        return new String(bytes, charset);
      }
    };
  }
}
