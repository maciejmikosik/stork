package com.mikosik.stork.build.tokenize;

import static com.mikosik.stork.build.tokenize.IntegerLiteral.literal;
import static com.mikosik.stork.build.tokenize.Label.label;
import static com.mikosik.stork.build.tokenize.StringLiteral.literal;
import static com.mikosik.stork.build.tokenize.Symbol.symbol;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Peekerator.peekerator;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isDoubleQuote;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNumeric;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.io.Ascii.isWhitespace;
import static com.mikosik.stork.problem.ProblemException.exception;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.illegalCode;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.illegalCodeInStringLiteral;

import java.math.BigInteger;
import java.util.Iterator;

import com.mikosik.stork.common.Peekerator;

public class Tokenizer implements Iterator<Token> {
  private final Peekerator<Byte> iterator;

  private Tokenizer(Peekerator<Byte> iterator) {
    this.iterator = iterator;
  }

  public static Iterator<Token> tokenize(Iterator<Byte> iterator) {
    return new Tokenizer(peekerator(iterator));
  }

  public boolean hasNext() {
    skipWhitespaces();
    return iterator.hasNext();
  }

  public Token next() {
    skipWhitespaces();
    var firstByte = iterator.peek();
    if (isDoubleQuote(firstByte)) {
      return nextStringLiteral();
    } else if (isLetter(firstByte)) {
      return nextLabel();
    } else if (isNumeric(firstByte)) {
      return nextIntegerLiteral();
    } else if ("(){}".indexOf(firstByte) >= 0) {
      return symbol(iterator.next());
    } else {
      throw exception(illegalCode(iterator.next()));
    }
  }

  private void skipWhitespaces() {
    while (iterator.hasNext() && isWhitespace(iterator.peek())) {
      iterator.next();
    }
  }

  private StringLiteral nextStringLiteral() {
    check(isDoubleQuote(iterator.next()));
    var builder = new StringBuilder();
    while (iterator.hasNext() && !isDoubleQuote(iterator.peek())) {
      var nextByte = iterator.next().byteValue();
      if (!isPrintable(nextByte)) {
        throw exception(illegalCodeInStringLiteral(nextByte));
      }
      builder.append((char) nextByte);
    }
    check(isDoubleQuote(iterator.next()));
    return literal(builder.toString());
  }

  private Label nextLabel() {
    var builder = new StringBuilder();
    while (iterator.hasNext() && isAlphanumeric(iterator.peek())) {
      builder.append((char) iterator.next().byteValue());
    }
    return label(builder.toString());
  }

  private IntegerLiteral nextIntegerLiteral() {
    var builder = new StringBuilder();
    while (iterator.hasNext() && isNumeric(iterator.peek())) {
      builder.append((char) iterator.next().byteValue());
    }
    return literal(new BigInteger(builder.toString()));
  }
}
