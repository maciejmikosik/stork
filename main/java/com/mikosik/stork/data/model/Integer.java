package com.mikosik.stork.data.model;

import java.math.BigInteger;

public class Integer implements Expression {
  public final BigInteger value;

  private Integer(BigInteger value) {
    this.value = value;
  }

  public static Expression integer(BigInteger value) {
    return new Integer(value);
  }

  public String toString() {
    return value.toString();
  }
}
