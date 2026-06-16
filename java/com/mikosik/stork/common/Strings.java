package com.mikosik.stork.common;

import static com.mikosik.stork.common.ImmutableList.toList;

import java.util.List;
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

  public static List<String> split(String regex, String string) {
    return toList(string.split(regex));
  }

  public static String reverse(String string) {
    return new StringBuilder(string)
        .reverse()
        .toString();
  }
}
