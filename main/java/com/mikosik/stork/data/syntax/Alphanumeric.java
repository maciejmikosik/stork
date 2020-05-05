package com.mikosik.stork.data.syntax;

public class Alphanumeric implements Syntax {
  public final String string;

  private Alphanumeric(String string) {
    this.string = string;
  }

  public static Alphanumeric alphanumeric(String string) {
    return new Alphanumeric(string);
  }

  public String toString() {
    return string;
  }
}
