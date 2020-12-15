package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.Ascii.isDoubleQuote;
import static com.mikosik.stork.common.Ascii.isWhitespace;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.PeekingInput.peeking;
import static com.mikosik.stork.data.syntax.Alphanumeric.alphanumeric;
import static com.mikosik.stork.data.syntax.Bracket.bracket;
import static com.mikosik.stork.data.syntax.BracketType.bracketByCharacter;
import static com.mikosik.stork.data.syntax.BracketType.isClosingBracket;
import static com.mikosik.stork.data.syntax.BracketType.isOpeningBracket;
import static com.mikosik.stork.data.syntax.Quote.quote;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.syntax.BracketType;
import com.mikosik.stork.data.syntax.Syntax;

public class Parser {
  public static Chain<Syntax> parse(Input source) {
    return parseSentence(peeking(source));
  }

  private static Chain<Syntax> parseSentence(PeekingInput input) {
    Chain<Syntax> sentence = empty();
    while (true) {
      int peek = input.peek();
      if (peek == -1) {
        break;
      } else if (isWhitespace(peek)) {
        input.read();
      } else if (isAlphanumeric(peek)) {
        sentence = sentence.add(parseAlphanumeric(input));
      } else if (isDoubleQuote(peek)) {
        sentence = sentence.add(parseDoubleQuote(input));
      } else if (isOpeningBracket(peek)) {
        sentence = sentence.add(parseBracket(input));
      } else if (isClosingBracket(peek)) {
        break;
      } else {
        throw new RuntimeException("unknown character " + peek);
      }
    }
    return sentence.reverse();
  }

  private static Syntax parseAlphanumeric(PeekingInput input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (isAlphanumeric(input.peek())) {
      buffer.write(input.read());
    }
    return alphanumeric(string(buffer.toByteArray()));
  }

  private static Syntax parseBracket(PeekingInput reading) {
    BracketType openingBracket = bracketByCharacter(reading.read());
    Chain<Syntax> sentence = parseSentence(reading);
    BracketType closingBracket = bracketByCharacter(reading.read());
    check(openingBracket == closingBracket);
    return bracket(openingBracket, sentence);
  }

  private static Syntax parseDoubleQuote(PeekingInput input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    input.read();
    while (!isDoubleQuote(input.peek())) {
      buffer.write(input.read());
    }
    input.read();
    return quote(string(buffer.toByteArray()));
  }

  private static String string(byte[] buffer) {
    return new String(buffer, US_ASCII);
  }
}
