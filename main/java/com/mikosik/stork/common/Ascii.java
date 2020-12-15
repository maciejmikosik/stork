package com.mikosik.stork.common;

public class Ascii {
  public static char SINGLE_QUOTE = '\'';
  public static char DOUBLE_QUOTE = '\"';

  public static boolean isLetter(int character) {
    return 'a' <= character && character <= 'z'
        || 'A' <= character && character <= 'Z';
  }

  public static boolean isDigit(int character) {
    return '0' <= character && character <= '9';
  }

  public static boolean isLetterOrDigit(int character) {
    return isLetter(character) || isDigit(character);
  }

  public static boolean isSign(int character) {
    return character == '+'
        || character == '-';
  }

  public static boolean isNumeric(int character) {
    return isDigit(character)
        || isSign(character);
  }

  public static boolean isAlphanumeric(int character) {
    return isLetter(character)
        || isNumeric(character);
  }

  public static boolean isDoubleQuote(int character) {
    return character == '\"';
  }

  public static boolean isWhitespace(int character) {
    return character == ' '
        || character == '\n'
        || character == '\r'
        || character == '\t';
  }
}
