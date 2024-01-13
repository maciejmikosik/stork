package com.mikosik.stork.compile;

import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.model.change.Changes.ifQuote;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;

public class Unquote {
  public static final Function<Expression, Expression> unquote = ifQuote(
      quote -> stork(quote.string));
}
