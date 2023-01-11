package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.tool.common.Constants.NONE;
import static com.mikosik.stork.tool.common.Constants.SOME;
import static com.mikosik.stork.tool.common.Morph.morphQuotes;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Quote;

public class Unquote {
  public static Module unquote(Module module) {
    return morphQuotes(Unquote::asStorkStream).in(module);
  }

  public static Expression unquote(Expression expression) {
    return morphQuotes(Unquote::asStorkStream).in(expression);
  }

  private static Expression asStorkStream(Quote quote) {
    Expression stream = NONE;
    for (char character : reverse(quote.string).toCharArray()) {
      stream = application(SOME, integer(character), stream);
    }
    return stream;
  }
}
