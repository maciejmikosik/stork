package com.mikosik.stork.data.syntax;

import static java.util.Arrays.stream;

public enum BracketType {
  ROUND('(', ')'), CURLY('{', '}');

  private final char opening, closing;

  BracketType(char opening, char closing) {
    this.opening = opening;
    this.closing = closing;
  }

  public char openingCharacter() {
    return opening;
  }

  public char closingCharacter() {
    return closing;
  }

  public static boolean isOpeningBracket(int character) {
    return stream(BracketType.values())
        .anyMatch(value -> value.opening == character);
  }

  public static boolean isClosingBracket(int character) {
    return stream(BracketType.values())
        .anyMatch(value -> value.closing == character);
  }

  public static BracketType bracketByCharacter(int character) {
    return stream(BracketType.values())
        .filter(type -> type.opening == character || type.closing == character)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("" + character));
  }
}
