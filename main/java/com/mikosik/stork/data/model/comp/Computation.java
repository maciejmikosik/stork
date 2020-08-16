package com.mikosik.stork.data.model.comp;

import static com.mikosik.stork.data.model.comp.Empty.empty;

import com.mikosik.stork.data.model.Expression;

public class Computation {
  public final Expression expression;
  public final Stack stack;

  private Computation(Expression expression, Stack stack) {
    this.expression = expression;
    this.stack = stack;
  }

  public static Computation computation(Expression expression, Stack stack) {
    return new Computation(expression, stack);
  }

  public static Computation computation(Expression expression) {
    return new Computation(expression, empty());
  }
}
