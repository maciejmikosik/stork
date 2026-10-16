package com.mikosik.stork.common.text;

public class Regex {
  public static final String LETTER = "[A-Za-z]";

  public static String oneOrMore(String pattern) {
    return "(" + pattern + ")+";
  }

  public static String zeroOrMore(String pattern) {
    return "(" + pattern + ")*";
  }

  public static String zeroOrOne(String pattern) {
    return "(" + pattern + ")?";
  }

  public static String separatedBy(String separator, String item) {
    return item + zeroOrMore(separator + item);
  }
}
