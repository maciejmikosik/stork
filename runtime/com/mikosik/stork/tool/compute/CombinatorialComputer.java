package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Combinator.Y;
import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Combinator;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;

public class CombinatorialComputer implements Computer {
  private CombinatorialComputer() {}

  public static Computer combinatorialComputer() {
    return new CombinatorialComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Combinator
        ? compute((Combinator) computation.expression, computation.stack)
        : computation;
  }

  private Computation compute(Combinator combinator, Stack stack) {
    int numberOfArguments = numberOfArguments(combinator);
    Expression[] arguments = new Expression[MAX_ARGUMENTS];
    for (int i = 0; i < numberOfArguments; i++) {
      arguments[i] = stack.argument();
      stack = stack.pop();
    }
    Expression x = arguments[0], y = arguments[1], z = arguments[2];

    switch (combinator) {
      case I:
        /** I(x) = x */
        return computation(x, stack);
      case Y:
        /** Y(x) = x(Y(x)) */
        return computation(application(x, application(Y, x)), stack);
      case K:
        /** K(x)(y) = x */
        return computation(x, stack);
      case S:
        /** S(x)(y)(z) = x(z)(y(z)) */
        return computation(application(application(x, z), application(y, z)), stack);
      case C:
        /** C(x)(y)(z) = x(z)(y) */
        return computation(application(application(x, z), y), stack);
      case B:
        /** B(x)(y)(z) = x(y(z)) */
        return computation(application(x, application(y, z)), stack);
      default:
        throw new RuntimeException();
    }
  }

  private static int numberOfArguments(Combinator combinator) {
    switch (combinator) {
      case I:
      case Y:
        return 1;
      case K:
        return 2;
      case S:
      case C:
      case B:
        return 3;
      default:
        throw new RuntimeException();
    }
  }

  private static final int MAX_ARGUMENTS = 3;
}
