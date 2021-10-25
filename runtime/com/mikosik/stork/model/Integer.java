package com.mikosik.stork.model;

import java.math.BigInteger;

public class Integer implements Expression {
  public final BigInteger value;

  private Integer(BigInteger value) {
    this.value = value;
  }

  public static Expression integer(BigInteger value) {
    return new Integer(value);
  }

  public static Expression integer(int value) {
    return new Integer(BigInteger.valueOf(value));
  }
}
