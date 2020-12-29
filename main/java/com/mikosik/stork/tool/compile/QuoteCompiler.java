package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.DOUBLE_QUOTE;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Quote.quote;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Expression;

public class QuoteCompiler implements Compiler<Expression> {
  public Expression compile(Input input) {
    return quote(string(parseQuote(input)));
  }

  private byte[] parseQuote(Input input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    check(input.read() == DOUBLE_QUOTE);
    while (input.peek() != -1 && input.peek() != DOUBLE_QUOTE) {
      buffer.write(input.read());
    }
    check(input.read() == DOUBLE_QUOTE);
    return buffer.toByteArray();
  }

  private static String string(byte[] buffer) {
    return new String(buffer, US_ASCII);
  }
}
