package com.mikosik.stork.compile.tokenize;

public enum Symbol implements Token {
  DOT('.');

  public final byte character;

  Symbol(char character) {
    this.character = (byte) character;
  }
}
