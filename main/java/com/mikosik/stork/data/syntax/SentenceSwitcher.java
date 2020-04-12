package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.data.syntax.BracketType.CURLY;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.Legal.isInteger;
import static com.mikosik.stork.data.syntax.Legal.isLabel;
import static com.mikosik.stork.data.syntax.Sentence.sentence;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SentenceSwitcher<R> {
  private BiFunction<Word, Sentence, R> labelOption;
  private BiFunction<Word, Sentence, R> integerOption;
  private BiFunction<Bracket, Sentence, R> roundBracketOption;
  private BiFunction<Bracket, Sentence, R> curlyBracketOption;
  private Supplier<R> emptyOption;

  private SentenceSwitcher() {}

  public static <R> SentenceSwitcher<R> sentenceSwitcherReturning(Class<R> resultType) {
    return new SentenceSwitcher<>();
  }

  public SentenceSwitcher<R> ifLabel(BiFunction<Word, Sentence, R> option) {
    this.labelOption = option;
    return this;
  }

  public SentenceSwitcher<R> ifInteger(BiFunction<Word, Sentence, R> option) {
    this.integerOption = option;
    return this;
  }

  public SentenceSwitcher<R> ifRoundBracket(BiFunction<Bracket, Sentence, R> option) {
    this.roundBracketOption = option;
    return this;
  }

  public SentenceSwitcher<R> ifCurlyBracket(BiFunction<Bracket, Sentence, R> option) {
    this.curlyBracketOption = option;
    return this;
  }

  public SentenceSwitcher<R> ifEmpty(Supplier<R> option) {
    this.emptyOption = option;
    return this;
  }

  public R apply(Sentence sentence) {
    if (!sentence.parts.available()) {
      return emptyOption.get();
    }
    Syntax head = sentence.parts.head();
    Sentence tail = sentence(sentence.parts.tail());
    if (head instanceof Word) {
      Word word = (Word) head;
      if (isLabel(word.string)) {
        return labelOption.apply(word, tail);
      } else if (isInteger(word.string)) {
        return integerOption.apply(word, tail);
      } else {
        throw new RuntimeException();
      }
    } else if (head instanceof Bracket) {
      Bracket bracket = (Bracket) head;
      if (bracket.type == ROUND) {
        return roundBracketOption.apply(bracket, tail);
      } else if (bracket.type == CURLY) {
        return curlyBracketOption.apply(bracket, tail);
      } else {
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }
  }
}
