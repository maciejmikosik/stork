package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.tool.common.Bridge.NONE;
import static com.mikosik.stork.tool.common.Bridge.SOME;
import static com.mikosik.stork.tool.link.Changes.changeQuote;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Quote;

public class Unquote {
  public static final Change<Expression> unquote = changeQuote(Unquote::asStorkStream);

  private static Expression asStorkStream(Quote quote) {
    Expression stream = NONE;
    for (char character : reverse(quote.string).toCharArray()) {
      stream = application(SOME, integer(character), stream);
    }
    return stream;
  }
}
