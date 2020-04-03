package com.mikosik.lang.compile;

import static com.mikosik.lang.compile.Bracket.bracket;
import static com.mikosik.lang.compile.Sentence.sentence;
import static com.mikosik.lang.compile.Word.word;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.lang.common.Stream;

public class Parser {
  public static Sentence parse(Stream stream) {
    return sentence(parseChildren(stream));
  }

  private static List<Syntax> parseChildren(Stream stream) {
    List<Syntax> children = new LinkedList<>();
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
    List<Syntax> children = parseChildren(stream);
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
