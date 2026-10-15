package com.mikosik.stork.model.token;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import java.math.BigInteger;

public class IntegerLiteral implements Token {
  public final BigInteger value;

  private IntegerLiteral(BigInteger value) {
    this.value = value;
  }

  public static IntegerLiteral literal(BigInteger value) {
    return new IntegerLiteral(value);
  }

  public static IntegerLiteral literal(long value) {
    return literal(BigInteger.valueOf(value));
  }

  public boolean equals(Object that) {
    return that instanceof IntegerLiteral
        && equals((IntegerLiteral) that);
  }

  private boolean equals(IntegerLiteral that) {
    return deepEquals(
        this.value,
        that.value);
  }

  public int hashCode() {
    return hash(value);
  }
}
