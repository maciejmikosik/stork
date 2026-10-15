package com.mikosik.stork.model.token;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

public class StringLiteral implements Token {
  public final String string;

  private StringLiteral(String string) {
    this.string = string;
  }

  public static StringLiteral literal(String string) {
    return new StringLiteral(string);
  }

  public boolean equals(Object that) {
    return that instanceof StringLiteral
        && equals((StringLiteral) that);
  }

  private boolean equals(StringLiteral that) {
    return deepEquals(
        this.string,
        that.string);
  }

  public int hashCode() {
    return hash(string);
  }
}
