package com.mikosik.stork.data.syntax;

public class Bracket implements Syntax {
  public final Sentence sentence;
  public final BracketType type;

  private Bracket(BracketType type, Sentence sentence) {
    this.type = type;
    this.sentence = sentence;
  }

  public static Bracket bracket(BracketType type, Sentence sentence) {
    return new Bracket(type, sentence);
  }

  public String toString() {
    return ""
        + type.openingCharacter()
        + sentence
        + type.closingCharacter();
  }
}
