package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.data.syntax.BracketType.CURLY;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.Legal.isInteger;
import static com.mikosik.stork.data.syntax.Legal.isLabel;
import static com.mikosik.stork.data.syntax.Sentence.sentence;
import static java.lang.String.format;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Switch {
  private final Sentence sentence;
  private final Optional<Object> result;

  private Switch(Sentence sentence, Optional<Object> result) {
    this.sentence = sentence;
    this.result = result;
  }

  private Switch withResult(Object object) {
    return new Switch(sentence, Optional.of(object));
  }

  public static Switch switchOn(Sentence sentence) {
    return new Switch(sentence, Optional.empty());
  }

  public Switch ifEmpty(Object handler) {
    return !result.isPresent()
        && !sentence.parts.available()
            ? withResult(handler)
            : this;
  }

  public Switch ifMulti(Function<Sentence, Object> handler) {
    return !result.isPresent()
        && sentence.parts.available()
        && sentence.parts.tail().available()
            ? new Switch(sentence, Optional.of(handler))
            : this;
  }

  public Switch ifLabel(BiFunction<Word, Sentence, Object> handler) {
    return !result.isPresent()
        && sentence.parts.available()
        && sentence.parts.head() instanceof Word
        && isLabel(((Word) sentence.parts.head()).string)
            ? withResult(handler.apply(
                (Word) sentence.parts.head(),
                sentence(sentence.parts.tail())))
            : this;
  }

  public Switch ifInteger(BiFunction<Word, Sentence, Object> handler) {
    return !result.isPresent()
        && sentence.parts.available()
        && sentence.parts.head() instanceof Word
        && isInteger(((Word) sentence.parts.head()).string)
            ? withResult(handler.apply(
                (Word) sentence.parts.head(),
                sentence(sentence.parts.tail())))
            : this;
  }

  public Switch ifRoundBracket(BiFunction<Bracket, Sentence, Object> handler) {
    return !result.isPresent()
        && sentence.parts.available()
        && sentence.parts.head() instanceof Bracket
        && ((Bracket) sentence.parts.head()).type == ROUND
            ? withResult(handler.apply(
                (Bracket) sentence.parts.head(),
                sentence(sentence.parts.tail())))
            : this;
  }

  public Switch ifCurlyBracket(BiFunction<Bracket, Sentence, Object> handler) {
    return !result.isPresent()
        && sentence.parts.available()
        && sentence.parts.head() instanceof Bracket
        && ((Bracket) sentence.parts.head()).type == CURLY
            ? withResult(handler.apply(
                (Bracket) sentence.parts.head(),
                sentence(sentence.parts.tail())))
            : this;
  }

  public <R> R elseFail() {
    return (R) result.orElseThrow(() -> new RuntimeException(
        format("cannot handle sentence '%s'", sentence)));
  }
}
