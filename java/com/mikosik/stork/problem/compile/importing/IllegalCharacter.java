package com.mikosik.stork.problem.compile.importing;

public class IllegalCharacter extends MalformedImportLine {
  public final byte character;

  private IllegalCharacter(String text, byte character) {
    super(text);
    this.character = character;
  }

  public static IllegalCharacter illegalCharacter(
      String text,
      byte character) {
    return new IllegalCharacter(text, character);
  }
}
