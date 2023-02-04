package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.common.Bridge.stork;
import static com.mikosik.stork.tool.link.Changes.changeQuote;

import com.mikosik.stork.model.Expression;

public class Unquote {
  public static final Change<Expression> unquote = changeQuote(
      quote -> stork(quote.string));
}
