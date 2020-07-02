package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Abort.abort;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class HumaneComputer implements Computer {
  private final Computer computer;

  private HumaneComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer humane(Computer computer) {
    return new HumaneComputer(computer);
  }

  public Expression compute(Expression expression) {
    Expression computed = computer.compute(expression);
    return computed instanceof Computation
        ? isHumane((Computation) computed)
            ? computed
            : abortIfComputation(expression)
        : computed instanceof Lambda
            ? abortIfComputation(expression)
            : computed;
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

  private static Expression abortIfComputation(Expression expression) {
    return expression instanceof Computation
        ? abort((Computation) expression)
        : expression;
  }
}
