package com.mikosik.stork.data.syntax;

import com.mikosik.stork.common.Chain;

public class Bracket implements Syntax {
  public final Chain<Syntax> sentence;
  public final BracketType type;

  private Bracket(BracketType type, Chain<Syntax> sentence) {
    this.type = type;
    this.sentence = sentence;
  }

  public static Bracket bracket(BracketType type, Chain<Syntax> sentence) {
    return new Bracket(type, sentence);
  }

  public String toString() {
    return ""
        + type.openingCharacter()
        + sentence
        + type.closingCharacter();
  }
}
