package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.Ascii.isWhitespace;
import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.reverse;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Reading.reading;
import static com.mikosik.stork.data.syntax.Alphanumeric.alphanumeric;
import static com.mikosik.stork.data.syntax.Bracket.bracket;
import static com.mikosik.stork.data.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.data.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.data.syntax.BracketType.isOpeningBracket;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Reading;
import com.mikosik.stork.data.syntax.BracketType;
import com.mikosik.stork.data.syntax.Syntax;

public class Parser {
  public static Chain<Syntax> parse(String source) {
    return parseSentence(reading(source));
  }

  private static Chain<Syntax> parseSentence(Reading reading) {
    Chain<Syntax> sentence = empty();
    while (reading.available()) {
      char character = reading.peek();
      if (isWhitespace(character)) {
        reading.read();
      } else if (isAlphanumeric(character)) {
        sentence = add(parseAlphanumeric(reading), sentence);
      } else if (isOpeningBracket(character)) {
        sentence = add(parseBracket(reading), sentence);
      } else if (isClosingBracket(character)) {
        break;
      } else {
        throw new RuntimeException("unknown character " + character);
      }
    }
    return reverse(sentence);
  }

  private static Syntax parseAlphanumeric(Reading reading) {
    StringBuilder builder = new StringBuilder();
    while (reading.available() && isAlphanumeric(reading.peek())) {
      builder.append(reading.read());
    }
    return alphanumeric(builder.toString());
  }

  private static Syntax parseBracket(Reading reading) {
    BracketType openingBracket = bracketByCharacter(reading.read());
    Chain<Syntax> sentence = parseSentence(reading);
    BracketType closingBracket = bracketByCharacter(reading.read());
    check(openingBracket == closingBracket);
    return bracket(openingBracket, sentence);
  }
}
