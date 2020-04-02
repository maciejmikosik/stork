package com.mikosik.lang.compile;

public class Word {
  public final String string;

  private Word(String string) {
    this.string = string;
  }

  public static Word word(String string) {
    return new Word(string);
  }
}
