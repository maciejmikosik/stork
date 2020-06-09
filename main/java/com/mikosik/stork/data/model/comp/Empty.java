package com.mikosik.stork.data.model.comp;

public class Empty implements Stack {
  private Empty() {}

  public static Stack empty() {
    return new Empty();
  }
}
