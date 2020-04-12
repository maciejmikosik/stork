package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.common.Ascii.isDigit;
import static com.mikosik.stork.common.Ascii.isLetter;
import static com.mikosik.stork.common.Ascii.isMinus;
import static com.mikosik.stork.common.Ascii.isWhitespace;
import static com.mikosik.stork.common.Strings.areAll;
import static com.mikosik.stork.common.Strings.startsWith;

import com.mikosik.stork.common.Ascii;

public class Legal {
  public static boolean isWordy(char character) {
    return isLetter(character)
        || isDigit(character)
        || isMinus(character);
  }

  public static boolean isWordSeparator(char character) {
    return isWhitespace(character);
  }

  public static boolean isLabel(String string) {
    return startsWith(Ascii::isLetter, string)
        && areAll(Ascii::isLetterOrDigit, string);
  }

  // TODO integer should allow '+' sign
  public static boolean isInteger(String string) {
    return startsWith(Ascii::isMinus, string)
        ? areAll(Ascii::isDigit, string.substring(1))
        : areAll(Ascii::isDigit, string);
  }
}