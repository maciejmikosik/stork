package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Strings.reverse;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Quote;
import com.mikosik.stork.model.Variable;
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

  private static final Variable SOME = variable("stork.stream.some");
  private static final Variable NONE = variable("stork.stream.none");
}
