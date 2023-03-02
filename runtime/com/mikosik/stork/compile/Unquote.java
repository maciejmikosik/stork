package com.mikosik.stork.compile;

import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.model.change.Changes.changeQuote;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.change.Change;

public class Unquote {
  public static final Change<Expression> unquote = changeQuote(
      quote -> stork(quote.string));
}
