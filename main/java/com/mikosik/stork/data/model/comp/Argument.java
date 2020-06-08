package com.mikosik.stork.data.model.comp;

import com.mikosik.stork.data.model.Expression;

public class Argument implements Stack {
  public final Expression expression;
  public final Stack stack;

  private Argument(Expression expression, Stack stack) {
    this.expression = expression;
    this.stack = stack;
  }

  public static Stack argument(Expression expression, Stack stack) {
    return new Argument(expression, stack);
  }
}
