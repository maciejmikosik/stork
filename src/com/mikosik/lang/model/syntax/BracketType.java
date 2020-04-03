package com.mikosik.lang.model.syntax;

import static java.util.Arrays.stream;

public enum BracketType {
  ROUND('(', ')');

  private final char opening, closing;

  BracketType(char opening, char closing) {
    this.opening = opening;
    this.closing = closing;
  }

  public static boolean isOpeningBracket(char character) {
    return stream(BracketType.values())
        .anyMatch(value -> value.opening == character);
  }

  public static boolean isClosingBracket(char character) {
    return stream(BracketType.values())
        .anyMatch(value -> value.closing == character);
  }

  public static BracketType bracketByCharacter(char character) {
    return stream(BracketType.values())
        .filter(type -> type.opening == character || type.closing == character)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("" + character));
  }
}
