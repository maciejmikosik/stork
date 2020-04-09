package com.mikosik.stork.model.syntax;

import static com.mikosik.stork.model.syntax.BracketType.CURLY;
import static com.mikosik.stork.model.syntax.BracketType.ROUND;
import static com.mikosik.stork.model.syntax.Sentence.sentence;

public class Visitor<T> {
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

  public static <T> T visit(Sentence sentence, Visitor<T> visitor) {
    Syntax head = sentence.parts.head();
    Sentence tail = sentence(sentence.parts.tail());
    if (head instanceof Word) {
      Word word = (Word) head;
      if (word.string.chars().allMatch(Visitor::isNumberCharacter)) {
        return visitor.visitInteger(word, tail);
      } else {
        return visitor.visitLabel(word, tail);
      }
    } else if (head instanceof Bracket) {
      Bracket bracket = (Bracket) head;
      if (bracket.type == ROUND) {
        return visitor.visitRound(bracket, tail);
      } else if (bracket.type == CURLY) {
        return visitor.visitCurly(bracket, tail);
      } else {
        return visitor.visitDefault(sentence);
      }
    } else {
      return visitor.visitDefault(sentence);
    }
  }

  private static Sentence join(Syntax syntax, Sentence sentence) {
    return sentence(sentence.parts.add(syntax));
  }

  // TODO gather functions checking word characters
  private static boolean isNumberCharacter(int character) {
    return '0' <= character && character <= '9'
        || character == '-';
  }
}
