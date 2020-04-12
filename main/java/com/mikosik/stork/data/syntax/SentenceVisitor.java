package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.data.syntax.BracketType.CURLY;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.Legal.isInteger;
import static com.mikosik.stork.data.syntax.Legal.isLabel;
import static com.mikosik.stork.data.syntax.Sentence.sentence;

public class SentenceVisitor<T> {
  protected T visitLabel(Word head, Sentence tail) {
    return visitDefault(join(head, tail));
  }

  protected T visitInteger(Word head, Sentence tail) {
    return visitDefault(join(head, tail));
  }

  protected T visitRound(Bracket head, Sentence tail) {
    return visitDefault(join(head, tail));
  }

  protected T visitCurly(Bracket head, Sentence tail) {
    return visitDefault(join(head, tail));
  }

  protected T visitDefault(Sentence sentence) {
    throw new RuntimeException();
  }

  public T visit(Sentence sentence) {
    Syntax head = sentence.parts.head();
    Sentence tail = sentence(sentence.parts.tail());
    if (head instanceof Word) {
      Word word = (Word) head;
      if (isLabel(word.string)) {
        return visitLabel(word, tail);
      } else if (isInteger(word.string)) {
        return visitInteger(word, tail);
      } else {
        return visitDefault(sentence);
      }
    } else if (head instanceof Bracket) {
      Bracket bracket = (Bracket) head;
      if (bracket.type == ROUND) {
        return visitRound(bracket, tail);
      } else if (bracket.type == CURLY) {
        return visitCurly(bracket, tail);
      } else {
        return visitDefault(sentence);
      }
    } else {
      return visitDefault(sentence);
    }
  }

  private static Sentence join(Syntax syntax, Sentence sentence) {
    return sentence(sentence.parts.add(syntax));
  }
}
