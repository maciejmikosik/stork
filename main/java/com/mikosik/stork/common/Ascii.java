package com.mikosik.stork.common;

public class Ascii {
  public static char SINGLE_QUOTE = '\'';
  public static char DOUBLE_QUOTE = '\"';

  public static boolean isLetter(char character) {
    return 'a' <= character && character <= 'z'
        || 'A' <= character && character <= 'Z';
  }

  public static boolean isDigit(char character) {
    return '0' <= character && character <= '9';
  }

  public static boolean isLetterOrDigit(char character) {
    return isLetter(character) || isDigit(character);
  }

  public static boolean isSign(char character) {
    return character == '+'
        || character == '-';
  }

  public static boolean isAlphanumeric(char character) {
    return isLetter(character)
        || isDigit(character)
        || isSign(character);
  }

  public static boolean isDoubleQuote(char character) {
    return character == '\"';
  }

  public static boolean isWhitespace(char character) {
    return character == ' '
        || character == '\n'
        || character == '\r'
        || character == '\t';
  }
}
