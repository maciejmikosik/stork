package com.mikosik.stork.compute;

import static com.mikosik.stork.model.Application.application;

import com.mikosik.stork.model.Expression;

public class Computations {
  public static Expression abort(Computation computation) {
    Expression expression = computation.expression;
    Stack stack = computation.stack;
    while (true) {
      if (stack.hasArgument()) {
        expression = application(expression, stack.argument());
      } else if (stack.hasFunction()) {
        expression = application(stack.function(), expression);
      } else {
        break;
      }
      stack = stack.pop();
    }
    return expression;
  }
}
