package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.common.Strings.areAll;
import static com.mikosik.stork.common.Strings.startsWith;
import static com.mikosik.stork.data.syntax.BracketType.CURLY;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static java.lang.String.format;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.Ascii;
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

  public Switch ifLabel(BiFunction<Alphanumeric, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Alphanumeric && isLabel(((Alphanumeric) head).string)
                ? withResult(handler.apply((Alphanumeric) head, tail))
                : this,
            () -> this);
  }

  private static boolean isLabel(String string) {
    return startsWith(Ascii::isLetter, string)
        && areAll(Ascii::isLetterOrDigit, string);
  }

  public Switch ifInteger(BiFunction<Alphanumeric, Chain<Syntax>, Object> handler) {
    return result.isPresent()
        ? this
        : sentence.visit(
            (head, tail) -> head instanceof Alphanumeric && isInteger(((Alphanumeric) head).string)
                ? withResult(handler.apply((Alphanumeric) head, tail))
                : this,
            () -> this);
  }

  private static boolean isInteger(String string) {
    return startsWith(Ascii::isSign, string)
        ? areAll(Ascii::isDigit, string.substring(1))
        : areAll(Ascii::isDigit, string);
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
