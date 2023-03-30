package com.mikosik.stork.compile;

import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.model.change.Changes.changeQuote;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;

public class Unquote {
  public static final Function<Expression, Expression> unquote = changeQuote(
      quote -> stork(quote.string));
}
