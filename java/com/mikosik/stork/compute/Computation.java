package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Stack.stack;

import com.mikosik.stork.model.Expression;

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
    return new Computation(expression, stack());
  }
}
