package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static java.lang.String.format;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Stack;

public class Innates {
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

  private static Innate unnamedComputeArgumentAt(int index) {
    return stack -> {
      Expression function = stack.argument();
      stack = stack.pop();
      for (int i = 1; i < index; i++) {
        function = application(function, stack.argument());
        stack = stack.pop();
      }
      Expression argument = stack.argument();
      stack = stack.pop();
      return computation(argument, stack.pushFunction(function));
    };
  }

  public static Innate rename(String name, Innate expression) {
    return new Innate() {
      public Computation compute(Stack stack) {
        return expression.compute(stack);
      }

      public String toString() {
        return name;
      }
    };
  }
}
