package com.mikosik.stork.common.text;

public class Regex {
  public static final String letter = "[A-Za-z]";

  public static String atLeastOne(String pattern) {
    return "(" + pattern + ")+";
  }

  public static String anyNumber(String pattern) {
    return "(" + pattern + ")*";
  }

  public static String maybe(String pattern) {
    return "(" + pattern + ")?";
  }

  public static String separated(String separator, String item) {
    return item + anyNumber(separator + item);
  }
}
