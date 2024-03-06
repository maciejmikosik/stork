package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.ByteToken.token;

import java.util.Iterator;

public class Parser {
  public static Iterator<Token> parse(Iterator<Byte> iterator) {
    return new Iterator<>() {
      public boolean hasNext() {
        return iterator.hasNext();
      }

      public Token next() {
        return token(iterator.next());
      }
    };
  }
}
