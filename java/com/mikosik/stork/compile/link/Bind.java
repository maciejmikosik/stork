package com.mikosik.stork.compile.link;

import static com.mikosik.stork.model.exp.Changes.deep;
import static com.mikosik.stork.model.exp.Changes.ifIdentifier;
import static com.mikosik.stork.model.exp.Identifier.identifier;

import com.mikosik.stork.model.exp.Expression;

public class Bind {
  public static Expression removeNamespaces(Expression expression) {
    return deep(ifIdentifier(identifier -> identifier(identifier.variable)))
        .apply(expression);
  }
}
