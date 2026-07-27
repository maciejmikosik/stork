package com.mikosik.stork.model.token;

public class StringLiteral implements Token {
  public final String string;

  private StringLiteral(String string) {
    this.string = string;
  }

  public static StringLiteral literal(String string) {
    return new StringLiteral(string);
  }
}
