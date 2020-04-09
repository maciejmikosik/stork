package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.model.syntax.Bracket.bracket;
import static com.mikosik.stork.model.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.model.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.model.syntax.BracketType.isOpeningBracket;
import static com.mikosik.stork.model.syntax.Sentence.sentence;
import static com.mikosik.stork.model.syntax.Word.word;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.Stream;
import com.mikosik.stork.model.syntax.Bracket;
import com.mikosik.stork.model.syntax.BracketType;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.model.syntax.Syntax;
import com.mikosik.stork.model.syntax.Word;

public class Parser {
  public static Sentence parse(String source) {
    return parseSentence(stream(source));
  }

  private static Sentence parseSentence(Stream stream) {
    List<Syntax> parts = new LinkedList<>();
    while (stream.available()) {
      if (isWhitespace(stream.peek())) {
        stream.read();
      } else if (isWordCharacter(stream.peek())) {
        parts.add(parseWord(stream));
      } else if (isOpeningBracket(stream.peek())) {
        parts.add(parseBracket(stream));
      } else if (isClosingBracket(stream.peek())) {
        break;
      } else {
        throw new RuntimeException("unknown character " + stream.peek());
      }
    }
    return sentence(parts);
  }

  private static Word parseWord(Stream stream) {
    StringBuilder builder = new StringBuilder();
    while (stream.available() && isWordCharacter(stream.peek())) {
      builder.append(stream.read());
    }
    return word(builder.toString());
  }

  private static Bracket parseBracket(Stream stream) {
    BracketType openingBracket = bracketByCharacter(stream.read());
    Sentence sentence = parseSentence(stream);
    BracketType closingBracket = bracketByCharacter(stream.read());
    check(openingBracket == closingBracket);
    return bracket(openingBracket, sentence);
  }

  // TODO gather functions checking word characters
  private static boolean isWhitespace(char peek) {
    return Character.isWhitespace(peek);
  }

  // TODO gather functions checking word characters
  private static boolean isWordCharacter(char character) {
    return 'a' <= character && character <= 'z'
        || 'A' <= character && character <= 'Z'
        || '0' <= character && character <= '9'
        || character == '-';
  }
}
