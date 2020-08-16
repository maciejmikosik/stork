package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;
import com.mikosik.stork.data.model.comp.Stack;

public class HumaneComputer implements Computer {
  private final Computer computer;

  private HumaneComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer humane(Computer computer) {
    return new HumaneComputer(computer);
  }

  public Computation compute(Computation computation) {
    while (!(computation.stack instanceof Empty)
        || computation.expression instanceof Variable
        || computation.expression instanceof Application) {
      Computation computed = computer.compute(computation);
      if (isHumane(computed)) {
        computation = computed;
      } else {
        break;
      }
    }
    return computation;
  }

  private static boolean isHumane(Computation computation) {
    return countParameters(computation) <= countArguments(computation);
  }

  private static int countArguments(Computation computation) {
    Stack stack = computation.stack;
    int count = 0;
    while (stack instanceof Argument) {
      Argument argument = (Argument) stack;
      stack = argument.stack;
      count++;
    }
    return count;
  }

  private static int countParameters(Computation computation) {
    Expression expression = computation.expression;
    int count = 0;
    while (expression instanceof Lambda) {
      Lambda lambda = (Lambda) expression;
      expression = lambda.body;
      count++;
    }
    return count;
  }
}
