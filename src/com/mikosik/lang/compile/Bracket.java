package com.mikosik.lang.compile;

public class Bracket implements Syntax {
  public final Sentence sentence;

  private Bracket(Sentence sentence) {
    this.sentence = sentence;
  }

  public static Bracket bracket(Sentence sentence) {
    return new Bracket(sentence);
  }
}
