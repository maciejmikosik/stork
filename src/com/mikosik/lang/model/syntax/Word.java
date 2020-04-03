package com.mikosik.lang.model.syntax;

public class Word implements Syntax {
  public final String string;

  private Word(String string) {
    this.string = string;
  }

  public static Word word(String string) {
    return new Word(string);
  }
}
