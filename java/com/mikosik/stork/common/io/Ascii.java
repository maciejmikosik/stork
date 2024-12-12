package com.mikosik.stork.common.io;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class Ascii {
  public static final byte SINGLE_QUOTE = '\'';
  public static final byte DOUBLE_QUOTE = '\"';

  public static boolean isAscii(byte character) {
    return 0 <= character;
  }

  public static boolean isPrintable(byte character) {
    return 32 <= character && character <= 126;
  }

  public static boolean isLetter(byte character) {
    return 'a' <= character && character <= 'z'
        || 'A' <= character && character <= 'Z';
  }

  public static boolean isDigit(byte character) {
    return '0' <= character && character <= '9';
  }

  public static boolean isSign(byte character) {
    return character == '+'
        || character == '-';
  }

  public static boolean isNumeric(byte character) {
    return isDigit(character)
        || isSign(character);
  }

  public static boolean isAlphanumeric(byte character) {
    return isLetter(character)
        || isNumeric(character);
  }

  public static boolean isDoubleQuote(byte character) {
    return character == '\"';
  }

  public static boolean isWhitespace(byte character) {
    return character == ' '
        || character == '\n'
        || character == '\r'
        || character == '\t';
  }

  public static String ascii(byte[] bytes) {
    return new String(bytes, US_ASCII);
  }

  public static byte[] bytes(String string) {
    return string.getBytes(US_ASCII);
  }
}
