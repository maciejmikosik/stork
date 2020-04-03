package com.mikosik.lang.compile;

import static com.mikosik.lang.compile.Bracket.bracket;
import static com.mikosik.lang.compile.Sentence.sentence;
import static com.mikosik.lang.compile.Word.word;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.lang.common.Stream;

public class Parser {
  public static Sentence parse(Stream stream) {
    return parseSentence(stream);
  }

  private static Sentence parseSentence(Stream stream) {
    List<Syntax> parts = new LinkedList<>();
    while (stream.available()) {
      if (isLetter(stream.peek())) {
        parts.add(parseWord(stream));
      } else if (isOpeningBracket(stream.peek())) {
        parts.add(parseBracket(stream));
      } else if (isClosingBracket(stream.peek())) {
        break;
      } else {
        throw new RuntimeException();
      }
    }
    return sentence(parts);
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
    Sentence sentence = parseSentence(stream);
    stream.read(); // TODO check type of bracket
    return bracket(sentence);
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
