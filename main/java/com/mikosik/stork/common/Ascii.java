package com.mikosik.stork.common;

public class Ascii {
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

  public static boolean isMinus(char character) {
    return character == '-';
  }

  public static boolean isWhitespace(char character) {
    return character == ' '
        || character == '\n'
        || character == '\r'
        || character == '\t';
  }
}
