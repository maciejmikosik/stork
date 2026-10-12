package com.mikosik.stork.model.token;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

public class Label implements Token {
  public String string;

  private Label(String string) {
    this.string = string;
  }

  public static Label label(String string) {
    return new Label(string);
  }

  public boolean equals(Object that) {
    return that instanceof Label
        && equals((Label) that);
  }

  private boolean equals(Label that) {
    return deepEquals(
        this.string,
        that.string);
  }

  public int hashCode() {
    return hash(string);
  }
}
