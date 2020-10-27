package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.comp.Argument.argument;
import static com.mikosik.stork.data.model.comp.Computation.computation;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Function;
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
    } else if (stack instanceof Function) {
      return compute(expression, (Function) stack);
    } else {
      return computer.compute(computation);
    }
  }

  private static Computation compute(Application application, Stack stack) {
    return computation(
        application.function,
        argument(application.argument, stack));
  }

  private static Computation compute(Expression expression, Function function) {
    return computation(
        application(function.expression, expression),
        function.stack);
  }
}
