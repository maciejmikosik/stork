package com.mikosik.stork.problem.compile.tokenize;

public class IllegalCharacterInString extends CannotTokenize {
  public final byte character;

  protected IllegalCharacterInString(byte character) {
    this.character = character;
  }

  public static IllegalCharacterInString illegalCharacterInString(byte character) {
    return new IllegalCharacterInString(character);
  }
}
