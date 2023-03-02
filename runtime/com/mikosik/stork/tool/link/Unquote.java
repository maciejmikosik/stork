package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.change.Changes.changeQuote;
import static com.mikosik.stork.tool.common.Bridge.stork;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.change.Change;

public class Unquote {
  public static final Change<Expression> unquote = changeQuote(
      quote -> stork(quote.string));
}
