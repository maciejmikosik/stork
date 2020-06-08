package com.mikosik.stork.data.model.comp;

import com.mikosik.stork.data.model.Expression;

public class Function implements Stack {
  public final Expression expression;
  public final Stack stack;

  private Function(Expression expression, Stack stack) {
    this.expression = expression;
    this.stack = stack;
  }

  public static Stack function(Expression expression, Stack stack) {
    return new Function(expression, stack);
  }
}
