package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Reading.reading;
import static com.mikosik.stork.data.syntax.Bracket.bracket;
import static com.mikosik.stork.data.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.data.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.data.syntax.BracketType.isOpeningBracket;
import static com.mikosik.stork.data.syntax.Legal.isWordSeparator;
import static com.mikosik.stork.data.syntax.Legal.isWordy;
import static com.mikosik.stork.data.syntax.Sentence.sentence;
import static com.mikosik.stork.data.syntax.Word.word;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Reading;
import com.mikosik.stork.data.syntax.Bracket;
import com.mikosik.stork.data.syntax.BracketType;
import com.mikosik.stork.data.syntax.Sentence;
import com.mikosik.stork.data.syntax.Syntax;
import com.mikosik.stork.data.syntax.Word;

public class Parser {
  public static Sentence parse(String source) {
    return parseSentence(reading(source));
  }

  private static Sentence parseSentence(Reading reading) {
    Chain<Syntax> parts = chain();
    while (reading.available()) {
      char character = reading.peek();
      if (isWordSeparator(character)) {
        reading.read();
      } else if (isWordy(character)) {
        parts = parts.add(parseWord(reading));
      } else if (isOpeningBracket(character)) {
        parts = parts.add(parseBracket(reading));
      } else if (isClosingBracket(character)) {
        break;
      } else {
        throw new RuntimeException("unknown character " + character);
      }
    }
    return sentence(parts.reverse());
  }

  private static Word parseWord(Reading reading) {
    StringBuilder builder = new StringBuilder();
    while (reading.available() && isWordy(reading.peek())) {
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
}
