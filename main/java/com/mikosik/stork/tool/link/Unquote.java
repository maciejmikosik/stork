package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.tool.common.Constants.NONE;
import static com.mikosik.stork.tool.common.Constants.SOME;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.tool.common.Traverser;

public class Unquote {
  public static Module unquote(Module module) {
    return new Traverser() {
      protected Expression traverse(Quote quote) {
        return asStorkStream(quote);
      }
    }.traverse(module);
  }

  private static Expression asStorkStream(Quote quote) {
    Expression stream = NONE;
    for (char character : reverse(quote.string).toCharArray()) {
      stream = application(SOME, integer(character), stream);
    }
    return stream;
  }
}
