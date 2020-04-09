package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Reading.reading;
import static com.mikosik.stork.model.syntax.Bracket.bracket;
import static com.mikosik.stork.model.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.model.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.model.syntax.BracketType.isOpeningBracket;
import static com.mikosik.stork.model.syntax.Sentence.sentence;
import static com.mikosik.stork.model.syntax.Word.word;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.Reading;
import com.mikosik.stork.model.syntax.Bracket;
import com.mikosik.stork.model.syntax.BracketType;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.model.syntax.Syntax;
import com.mikosik.stork.model.syntax.Word;

public class Parser {
  public static Sentence parse(String source) {
    return parseSentence(reading(source));
  }

  private static Sentence parseSentence(Reading reading) {
    List<Syntax> parts = new LinkedList<>();
    while (reading.available()) {
      if (isWhitespace(reading.peek())) {
        reading.read();
      } else if (isWordCharacter(reading.peek())) {
        parts.add(parseWord(reading));
      } else if (isOpeningBracket(reading.peek())) {
        parts.add(parseBracket(reading));
      } else if (isClosingBracket(reading.peek())) {
        break;
      } else {
        throw new RuntimeException("unknown character " + reading.peek());
      }
    }
    return sentence(parts);
  }

  private static Word parseWord(Reading reading) {
    StringBuilder builder = new StringBuilder();
    while (reading.available() && isWordCharacter(reading.peek())) {
      builder.append(reading.read());
    }
    return word(builder.toString());
  }

  private static Bracket parseBracket(Reading reading) {
    BracketType openingBracket = bracketByCharacter(reading.read());
    Sentence sentence = parseSentence(reading);
    BracketType closingBracket = bracketByCharacter(reading.read());
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
