package com.mikosik.stork.problem.compile.importing;

public class IllegalCharacterInImport extends CannotImport {
  public final String text;
  public final byte character;

  private IllegalCharacterInImport(String text, byte character) {
    this.text = text;
    this.character = character;
  }

  public static IllegalCharacterInImport illegalCharacterInImport(
      String text,
      byte character) {
    return new IllegalCharacterInImport(text, character);
  }
}
