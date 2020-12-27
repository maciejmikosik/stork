package com.mikosik.stork.tool.common;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Operands.operands;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Alien;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class Aliens {
  public static Expression computeArguments(int number, Expression expression) {
    for (int i = number; i >= 1; i--) {
      expression = application(computeArgumentAt(i), expression);
    }
    return expression;
  }

  public static Expression computeArgumentAt(int index) {
    return rename(
        format("ARG_%d", index),
        unnamedComputeArgumentAt(index));
  }

  private static Alien unnamedComputeArgumentAt(int index) {
    return stack -> {
      Operands operands = operands(stack);
      Expression function = operands.next();
      for (int i = 1; i < index; i++) {
        function = application(function, operands.next());
      }
      Expression argument = operands.next();
      Stack newStack = operands.stack();
      return computation(argument, function(function, newStack));
    };
  }

  public static Alien rename(String name, Alien expression) {
    return new Alien() {
      public Computation compute(Stack stack) {
        return expression.compute(stack);
      }

      public String toString() {
        return name;
      }
    };
  }
}
