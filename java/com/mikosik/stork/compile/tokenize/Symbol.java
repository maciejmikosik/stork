package com.mikosik.stork.compile.tokenize;

public class Symbol implements Token {
  public final Byte character;

  private Symbol(Byte character) {
    this.character = character;
  }

  public static Symbol symbol(Byte character) {
    return new Symbol(character);
  }
}
