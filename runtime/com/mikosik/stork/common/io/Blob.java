package com.mikosik.stork.common.io;

import java.util.Arrays;

public class Blob {
  public final byte[] bytes;

  private Blob(byte[] bytes) {
    this.bytes = bytes;
  }

  public static Blob blob(byte[] bytes) {
    return new Blob(bytes);
  }

  public boolean equals(Object object) {
    return object instanceof Blob
        && equals((Blob) object);
  }

  private boolean equals(Blob blob) {
    return Arrays.equals(bytes, blob.bytes);
  }

  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  public String toString() {
    return Arrays.toString(bytes);
  }
}
