package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Output.output;

import java.io.ByteArrayOutputStream;

public class Buffer {
  private final ByteArrayOutputStream data = new ByteArrayOutputStream();

  public static Buffer newBuffer() {
    return new Buffer();
  }

  public Output asOutput() {
    return output(data);
  }

  // TODO replace by asInput
  public byte[] bytes() {
    return data.toByteArray();
  }
}
