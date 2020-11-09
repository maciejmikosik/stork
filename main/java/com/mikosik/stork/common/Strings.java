package com.mikosik.stork.common;

import java.util.function.Predicate;

public class Strings {
  public static boolean isNotEmpty(String string) {
    return string.length() > 0;
  }

  public static boolean startsWith(Predicate<Character> predicate, String string) {
    return !string.isEmpty()
        && predicate.test(string.charAt(0));
  }

  public static boolean areAll(Predicate<Character> predicate, String string) {
    return string.chars()
        .mapToObj(i -> (char) i)
        .allMatch(predicate);
  }

  public static String reverse(String string) {
    return new StringBuilder(string)
        .reverse()
        .toString();
  }

  public static String line(String string) {
    return string + "\n";
  }
}
