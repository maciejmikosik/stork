package com.mikosik.stork.compile.tokenize;

public class Label implements Token {
  public String string;

  private Label(String string) {
    this.string = string;
  }

  public static Label label(String string) {
    return new Label(string);
  }
}
