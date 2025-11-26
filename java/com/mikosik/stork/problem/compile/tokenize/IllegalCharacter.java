package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static java.lang.Byte.toUnsignedInt;

public class IllegalCharacter implements CannotTokenize {
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

  public String getMessage() {
    return "%sillegal %s character %s".formatted(
        describeLocation(),
        describeCategory(),
        describeValue());
  }

  private String describeLocation() {
    return inString
        ? "string contains "
        : "";
  }

  private String describeCategory() {
    return isAscii(character)
        ? isPrintable(character)
            ? "ascii"
            : "non-printable ascii"
        : "non-ascii";
  }

  private String describeValue() {
    return isPrintable(character)
        ? "[%c]".formatted(character)
        : "with decimal value of [%d]".formatted(toUnsignedInt(character));
  }
}
