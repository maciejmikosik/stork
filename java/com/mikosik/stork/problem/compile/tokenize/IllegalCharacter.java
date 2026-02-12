package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.problem.Characters.describeCharacter;

import com.mikosik.stork.common.Description;

public class IllegalCharacter extends CannotTokenize {
  public final byte character;
  public final boolean inString;

  protected IllegalCharacter(byte character, boolean inString) {
    this.character = character;
    this.inString = inString;
  }

  public static IllegalCharacter illegalCharacter(byte character) {
    return new IllegalCharacter(character, false);
  }

  public static IllegalCharacter illegalStringCharacter(byte character) {
    return new IllegalCharacter(character, true);
  }

  public Description describe() {
    return description("%sillegal %s".formatted(
        describeLocation(),
        describeCharacter(character)));
  }

  private String describeLocation() {
    return inString
        ? "string contains "
        : "";
  }
}
