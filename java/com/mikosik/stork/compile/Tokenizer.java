package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Peekerator.peekerator;
import static com.mikosik.stork.common.err.Throwables.check;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isDoubleQuote;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNumeric;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.io.Ascii.isWhitespace;
import static com.mikosik.stork.compile.err.CompilerException.exception;
import static com.mikosik.stork.compile.err.Problems.illegalCharacterInCode;
import static com.mikosik.stork.compile.err.Problems.illegalCharacterInStringLiteral;
import static com.mikosik.stork.model.token.IntegerLiteral.literal;
import static com.mikosik.stork.model.token.Label.label;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.model.token.Punctuator.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.LEFT_ROUND_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_ROUND_BRACKET;
import static com.mikosik.stork.model.token.StringLiteral.literal;

import java.math.BigInteger;
import java.util.Iterator;

import com.mikosik.stork.common.col.Peekerator;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Token;

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
    } else {
      return switch (iterator.next()) {
        case '(' -> LEFT_ROUND_BRACKET;
        case ')' -> RIGHT_ROUND_BRACKET;
        case '{' -> LEFT_CURLY_BRACKET;
        case '}' -> RIGHT_CURLY_BRACKET;
        case '.' -> DOT;
        default -> throw exception(illegalCharacterInCode()
            .object(firstByte)
            .build());
      };
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
        throw exception(illegalCharacterInStringLiteral()
            .object(nextByte)
            .build());
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
