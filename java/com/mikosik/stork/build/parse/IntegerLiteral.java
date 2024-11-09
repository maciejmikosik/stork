package com.mikosik.stork.build.parse;

import java.math.BigInteger;

public class IntegerLiteral implements Token {
  public final BigInteger value;

  private IntegerLiteral(BigInteger value) {
    this.value = value;
  }

  public static IntegerLiteral literal(BigInteger value) {
    return new IntegerLiteral(value);
  }
}
