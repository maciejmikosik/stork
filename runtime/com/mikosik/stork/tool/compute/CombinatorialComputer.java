package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Combinator.Y;
import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Combinator;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;

public class CombinatorialComputer implements Computer {
  private final Computer computer;

  private CombinatorialComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer combinatorial(Computer computer) {
    return new CombinatorialComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Combinator
        ? compute((Combinator) computation.expression, computation.stack)
        : computer.compute(computation);
  }

  private Computation compute(Combinator combinator, Stack stack) {
    switch (combinator) {

      case I:
        /** I(x) = x */
        return computation(
            stack.argument(),
            stack.pop());

      case K:
        /** K(x)(y) = x */
        return computation(
            stack.argument(),
            stack.pop().pop());

      case S:
      case C:
      case B:
        return computeSCB(combinator, stack);

      case Y:
        /** Y(f) = f(Y(f)) */
        Expression f = stack.argument();
        stack = stack.pop();
        stack = stack.pushArgument(application(Y, f));
        return computation(f, stack);

      default:
        throw new RuntimeException();
    }
  }

  private static Computation computeSCB(Combinator combinator, Stack stack) {
    Expression x = stack.argument();
    stack = stack.pop();
    Expression y = stack.argument();
    stack = stack.pop();
    Expression z = stack.argument();
    stack = stack.pop();

    switch (combinator) {
      case S:
        /** S(x)(y)(z) = x(z)(y(z)) */
        stack = stack
            .pushArgument(application(y, z))
            .pushArgument(z);
        return computation(x, stack);

      case C:
        /** C(x)(y)(z) = x(z)(y) */
        stack = stack
            .pushArgument(y)
            .pushArgument(z);
        return computation(x, stack);

      case B:
        /** B(x)(y)(z) = x(y(z)) */
        stack = stack.pushArgument(application(y, z));
        return computation(x, stack);

      default:
        throw new RuntimeException();
    }
  }
}
