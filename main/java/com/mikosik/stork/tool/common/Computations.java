package com.mikosik.stork.tool.common;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Switch.switchOn;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;
import com.mikosik.stork.data.model.comp.Stack;

public class Computations {
  /**
   * @return current state of {@code computation} by moving up stack
   */
  public static Expression abort(Computation computation) {
    Computation aborting = computation;
    while (!(aborting.stack instanceof Empty)) {
      aborting = moveUpStack(aborting);
    }
    return aborting.expression;
  }

  private static Computation moveUpStack(Computation computation) {
    return switchOn(computation.stack)
        .ifArgument(argument -> computation(
            application(computation.expression, argument.expression),
            argument.stack))
        .ifFunction(function -> computation(
            application(function.expression, computation.expression),
            function.stack))
        .elseFail();
  }

  public static boolean isComputable(Expression expression) {
    return expression instanceof Variable
        || expression instanceof Application;
  }

  public static boolean isComputable(Computation computation) {
    return !(computation.stack instanceof Empty)
        || isComputable(computation.expression);
  }

  public static boolean isHumane(Computation computation) {
    return countParameters(computation) <= countArguments(computation);
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

  public static String print(Computation computation) {
    return Expressions.print(abort(mark(computation)));
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }
}
