package com.mikosik.stork.common.text;

public class Regex {
  public static final String LETTER = "[A-Za-z]";
  public static final String SIGN = "[+-]";
  public static final String DIGIT = "[0-9]";

  public static String oneOrMore(String pattern) {
    return "(" + pattern + ")+";
  }

  public static String zeroOrMore(String pattern) {
    return "(" + pattern + ")*";
  }

  public static String zeroOrOne(String pattern) {
    return "(" + pattern + ")?";
  }

  public static String or(String patternA, String patternB) {
    return "(" + patternA + "|" + patternB + ")";
  }

  public static String separatedBy(String separator, String item) {
    return item + zeroOrMore(separator + item);
  }
}
