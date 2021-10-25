package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.common.InnateBuilder.innate;

import com.mikosik.stork.model.Expression;

public class Eager {
  public static Expression eager(int number, Expression expression) {
    for (int i = number; i >= 1; i--) {
      expression = application(eagerAt(i), expression);
    }
    return expression;
  }

  private static Expression eagerAt(int index) {
    return innate()
        .name("$EAGER_" + index)
        .logic(stack -> {
          Expression function = stack.argument();
          stack = stack.pop();
          for (int i = 1; i < index; i++) {
            function = application(function, stack.argument());
            stack = stack.pop();
          }
          Expression argument = stack.argument();
          stack = stack.pop();
          return computation(argument, stack.pushFunction(function));
        })
        .build();
  }
}
