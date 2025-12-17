package com.mikosik.stork.problem.compile.importing;

import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.problem.Description;

public class IllegalCharacter extends CannotImport {
  private final String text;
  private final byte character;

  private IllegalCharacter(String text, byte character) {
    this.text = text;
    this.character = character;
  }

  public static IllegalCharacter illegalCharacter(String text, byte character) {
    return new IllegalCharacter(text, character);
  }

  public Description describe() {
    return description("import [%s] contains illegal character [%c]"
        .formatted(text, character));
  }
}
