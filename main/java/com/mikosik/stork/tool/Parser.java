package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.Ascii.isWhitespace;
import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.reverse;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Reading.reading;
import static com.mikosik.stork.data.syntax.Bracket.bracket;
import static com.mikosik.stork.data.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.data.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.data.syntax.BracketType.isOpeningBracket;
import static com.mikosik.stork.data.syntax.Word.word;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Reading;
import com.mikosik.stork.data.syntax.Bracket;
import com.mikosik.stork.data.syntax.BracketType;
import com.mikosik.stork.data.syntax.Syntax;
import com.mikosik.stork.data.syntax.Word;

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
        sentence = add(parseWord(reading), sentence);
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

  private static Word parseWord(Reading reading) {
    StringBuilder builder = new StringBuilder();
    while (reading.available() && isAlphanumeric(reading.peek())) {
      builder.append(reading.read());
    }
    return word(builder.toString());
  }

  private static Bracket parseBracket(Reading reading) {
    BracketType openingBracket = bracketByCharacter(reading.read());
    Chain<Syntax> sentence = parseSentence(reading);
    BracketType closingBracket = bracketByCharacter(reading.read());
    check(openingBracket == closingBracket);
    return bracket(openingBracket, sentence);
  }
}
