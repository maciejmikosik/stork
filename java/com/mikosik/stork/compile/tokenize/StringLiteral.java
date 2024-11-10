package com.mikosik.stork.compile.tokenize;

public class StringLiteral implements Token {
  public final String string;

  private StringLiteral(String string) {
    this.string = string;
  }

  public static StringLiteral literal(String string) {
    return new StringLiteral(string);
  }
}
