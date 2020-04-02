package com.mikosik.lang.compile;

import static com.mikosik.lang.compile.Bracket.bracket;
import static com.mikosik.lang.compile.Unit.unit;
import static com.mikosik.lang.compile.Word.word;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.lang.common.Stream;

public class Parser {
  public static Unit parse(Stream stream) {
    return unit(parseChildren(stream));
  }

  private static List<Object> parseChildren(Stream stream) {
    List<Object> children = new LinkedList<>();
    while (stream.available()) {
      if (isLetter(stream.peek())) {
        children.add(parseWord(stream));
      } else if (isOpeningBracket(stream.peek())) {
        children.add(parseBracket(stream));
      } else if (isClosingBracket(stream.peek())) {
        return children;
      } else {
        throw new RuntimeException();
      }
    }
    return children;
  }

  private static Word parseWord(Stream stream) {
    StringBuilder builder = new StringBuilder();
    while (isLetter(stream.peek())) {
      builder.append(stream.read());
    }
    return word(builder.toString());
  }

  private static Bracket parseBracket(Stream stream) {
    stream.read(); // TODO check type of bracket
    List<Object> children = parseChildren(stream);
    stream.read(); // TODO check type of bracket
    return bracket(children);
  }

  private static boolean isLetter(char character) {
    return 'a' <= character && character <= 'z'
        || 'A' <= character && character <= 'Z';
  }

  private static boolean isOpeningBracket(char character) {
    return character == '(';
  }

  private static boolean isClosingBracket(char character) {
    return character == ')';
  }
}
