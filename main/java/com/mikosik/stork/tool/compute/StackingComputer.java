package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.comp.Computation.computation;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class StackingComputer implements Computer {
  private final Computer computer;

  private StackingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer stacking(Computer computer) {
    return new StackingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Expression expression = computation.expression;
    Stack stack = computation.stack;
    if (expression instanceof Application) {
      return compute((Application) expression, stack);
    } else if (expression instanceof Variable) {
      return computer.compute(computation);
    } else if (stack.hasFunction()) {
      return computation(
          application(stack.function(), expression),
          stack.pop());
    } else {
      return computer.compute(computation);
    }
  }

  private static Computation compute(Application application, Stack stack) {
    return computation(
        application.function,
        stack.pushArgument(application.argument));
  }
}
