package com.mikosik.stork.problem.compile.tokenize;

public class IllegalCharacterInCode extends CannotTokenize {
  public final byte character;

  protected IllegalCharacterInCode(byte character) {
    this.character = character;
  }

  public static IllegalCharacterInCode illegalCharacterInCode(byte character) {
    return new IllegalCharacterInCode(character);
  }
}
