package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Blob.blob;
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

  public Blob toBlob() {
    return blob(data.toByteArray());
  }
}
