package com.mikosik.lang.common;

public class Stream {
  private final String string;
  private int index = 0;

  private Stream(String string) {
    this.string = string;
  }

  public static Stream stream(String string) {
    return new Stream(string);
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
