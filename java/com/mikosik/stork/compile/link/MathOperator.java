package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compile.link.Bridge.stork;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Operator;

public enum MathOperator implements Operator {
  EQUAL,
  COMPARE,
  NEGATE,
  ADD,
  MULTIPLY,
  DIVIDE;

  public Computation compute(Stack stack) {
    int nArguments = nArguments();
    var arguments = new Integer[nArguments];
    for (int iArgument = 0; iArgument < nArguments; iArgument++) {
      if (stack instanceof Argument argument
          && argument.expression instanceof Integer integer) {
        arguments[iArgument] = integer;
        stack = argument.previous;
      } else {
        throw cannotCompute();
      }
    }
    return computation(compute(arguments), stack);
  }

  private int nArguments() {
    return switch (this) {
      case NEGATE -> 1;
      case EQUAL, COMPARE, ADD, MULTIPLY, DIVIDE -> 2;
    };
  }

  private Expression compute(Integer[] arguments) {
    var x = arguments[0].value;
    var y = arguments.length >= 2 ? arguments[1].value : null;
    return switch (this) {
      case EQUAL -> stork(x.equals(y));
      case COMPARE -> stork(x.compareTo(y) < 0);
      case NEGATE -> integer(x.negate());
      case ADD -> integer(x.add(y));
      case MULTIPLY -> integer(x.multiply(y));
      case DIVIDE -> integer(x.divide(y));
      default -> throw cannotCompute();
    };
  }
}
