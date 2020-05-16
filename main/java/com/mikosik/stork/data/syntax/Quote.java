package com.mikosik.stork.data.syntax;

import static java.lang.String.format;

public class Quote implements Syntax {
  public final String ascii;

  private Quote(String ascii) {
    this.ascii = ascii;
  }

  public static Quote quote(String ascii) {
    return new Quote(ascii);
  }

  public String toString() {
    return format("\"%s\"", ascii);
  }
}
