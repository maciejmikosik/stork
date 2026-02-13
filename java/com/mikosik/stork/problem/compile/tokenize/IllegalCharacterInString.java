package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.problem.Characters.describeCharacter;

import com.mikosik.stork.common.Description;

public class IllegalCharacterInString extends CannotTokenize {
  public final byte character;

  protected IllegalCharacterInString(byte character) {
    this.character = character;
  }

  public static IllegalCharacterInString illegalCharacterInString(byte character) {
    return new IllegalCharacterInString(character);
  }

  public Description describe() {
    return description("string contains illegal %s".formatted(
        describeCharacter(character)));
  }
}
