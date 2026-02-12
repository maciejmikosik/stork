package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.problem.Characters.describeCharacter;

import com.mikosik.stork.common.Description;

public class IllegalCharacterInCode extends CannotTokenize {
  public final byte character;

  protected IllegalCharacterInCode(byte character) {
    this.character = character;
  }

  public static IllegalCharacterInCode illegalCharacterInCode(byte character) {
    return new IllegalCharacterInCode(character);
  }

  public Description describe() {
    return description("code contains illegal %s".formatted(
        describeCharacter(character)));
  }
}
