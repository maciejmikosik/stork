package com.mikosik.stork.common;

public class Reading {
  private final String string;
  private int index = 0;

  private Reading(String string) {
    this.string = string;
  }

  public static Reading reading(String string) {
    return new Reading(string);
  }

  public char peek() {
    return string.charAt(index);
  }

  public char read() {
    return string.charAt(index++);
  }

  public boolean available() {
    return index < string.length();
  }
}
