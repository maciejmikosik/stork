package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.data.syntax.BracketType.CURLY;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.Legal.isInteger;
import static com.mikosik.stork.data.syntax.Legal.isLabel;
import static java.lang.String.format;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.Chain;

public class Switch {
  private final Chain<Syntax> sentence;
  private final Optional<Object> result;

  private Switch(Chain<Syntax> sentence, Optional<Object> result) {
    this.sentence = sentence;
    this.result = result;
  }

  private Switch withResult(Object object) {
    return new Switch(sentence, Optional.of(object));
  }

  public static Switch switchOn(Chain<Syntax> sentence) {
    return new Switch(sentence, Optional.empty());
  }

  public Switch ifEmpty(Object handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> this,
            () -> withResult(handler));
  }

  public Switch ifSentence(Function<Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> tail.visit(
                (head2, tail2) -> withResult(handler.apply(sentence)),
                () -> this),
            () -> this);
  }

  public Switch ifLabel(BiFunction<Word, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Word && isLabel(((Word) head).string)
                ? withResult(handler.apply((Word) head, tail))
                : this,
            () -> this);
  }

  public Switch ifInteger(BiFunction<Word, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Word && isInteger(((Word) head).string)
                ? withResult(handler.apply((Word) head, tail))
                : this,
            () -> this);
  }

  public Switch ifRoundBracket(BiFunction<Bracket, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Bracket && ((Bracket) head).type == ROUND
                ? withResult(handler.apply((Bracket) head, tail))
                : this,
            () -> this);
  }

  public Switch ifCurlyBracket(BiFunction<Bracket, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Bracket && ((Bracket) head).type == CURLY
                ? withResult(handler.apply((Bracket) head, tail))
                : this,
            () -> this);
  }

  public <R> R elseFail() {
    return (R) result.orElseThrow(() -> new RuntimeException(
        format("cannot handle sentence '%s'", sentence)));
  }
}
