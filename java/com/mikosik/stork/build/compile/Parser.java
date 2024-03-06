package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.ByteToken.token;
import static com.mikosik.stork.build.compile.StringLiteral.literal;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.PeekableIterator.peekable;
import static com.mikosik.stork.common.io.Ascii.isDoubleQuote;

import java.util.Iterator;

import com.mikosik.stork.common.PeekableIterator;

public class Parser {
  public static Iterator<Token> parse(Iterator<Byte> iterator) {
    return parse(peekable(iterator));
  }

  private static Iterator<Token> parse(PeekableIterator<Byte> iterator) {
    return new Iterator<>() {
      public boolean hasNext() {
        return iterator.hasNext();
      }

      public Token next() {
        var firstByte = iterator.peek();
        if (isDoubleQuote(firstByte)) {
          return parseStringLiteral(iterator);
        } else {
          return token(iterator.next());
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
}
