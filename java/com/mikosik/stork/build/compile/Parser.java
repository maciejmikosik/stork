package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.IntegerLiteral.literal;
import static com.mikosik.stork.build.compile.Label.label;
import static com.mikosik.stork.build.compile.StringLiteral.literal;
import static com.mikosik.stork.build.compile.Symbol.symbol;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.PeekableIterator.peekable;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isDoubleQuote;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNumeric;
import static com.mikosik.stork.common.io.Ascii.isWhitespace;

import java.math.BigInteger;
import java.util.Iterator;

import com.mikosik.stork.common.PeekableIterator;

public class Parser {
  public static Iterator<Token> parse(Iterator<Byte> iterator) {
    return parse(peekable(iterator));
  }

  private static Iterator<Token> parse(PeekableIterator<Byte> iterator) {
    return new Iterator<>() {
      public boolean hasNext() {
        skipWhitespaces();
        return iterator.hasNext();
      }

      public Token next() {
        skipWhitespaces();
        var firstByte = iterator.peek();
        if (isDoubleQuote(firstByte)) {
          return parseStringLiteral(iterator);
        } else if (isLetter(firstByte)) {
          return parseLabel(iterator);
        } else if (isNumeric(firstByte)) {
          return parseIntegerLiteral(iterator);
        } else {
          return symbol(iterator.next());
        }
      }

      private void skipWhitespaces() {
        while (iterator.hasNext() && isWhitespace(iterator.peek())) {
          iterator.next();
        }
      }
    };
  }

  private static StringLiteral parseStringLiteral(PeekableIterator<Byte> iterator) {
    check(isDoubleQuote(iterator.next()));
    var builder = new StringBuilder();
    while (iterator.hasNext() && !isDoubleQuote(iterator.peek())) {
      builder.append((char) iterator.next().byteValue());
    }
    check(isDoubleQuote(iterator.next()));
    return literal(builder.toString());
  }

  private static Label parseLabel(PeekableIterator<Byte> iterator) {
    var builder = new StringBuilder();
    while (iterator.hasNext() && isAlphanumeric(iterator.peek())) {
      builder.append((char) iterator.next().byteValue());
    }
    return label(builder.toString());
  }

  private static IntegerLiteral parseIntegerLiteral(PeekableIterator<Byte> iterator) {
    var builder = new StringBuilder();
    while (iterator.hasNext() && isNumeric(iterator.peek())) {
      builder.append((char) iterator.next().byteValue());
    }
    return literal(new BigInteger(builder.toString()));
  }
}
