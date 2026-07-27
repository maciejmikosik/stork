package com.mikosik.stork.model.exp;

public class Quote implements Expression {
  public final String string;

  private Quote(String string) {
    this.string = string;
  }

  public static Expression quote(String string) {
    return new Quote(string);
  }
}
