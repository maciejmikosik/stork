package com.mikosik.stork.model.token;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

public class Identifier implements Token {
  public String string;

  private Identifier(String string) {
    this.string = string;
  }

  public static Identifier identifier(String string) {
    return new Identifier(string);
  }

  public boolean equals(Object that) {
    return that instanceof Identifier
        && equals((Identifier) that);
  }

  private boolean equals(Identifier that) {
    return deepEquals(
        this.string,
        that.string);
  }

  public int hashCode() {
    return hash(string);
  }
}
