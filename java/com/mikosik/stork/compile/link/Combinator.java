package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;

import java.util.Optional;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;

public enum Combinator implements Operator {
  /** I(x) = x */
  I,
  /** K(x)(y) = x */
  K,
  /** S(x)(y)(z) = x(z)(y(z)) */
  S,
  /** C(x)(y)(z) = x(z)(y) */
  C,
  /** B(x)(y)(z) = x(y(z)) */
  B;

  public Optional<Computation> compute(Stack stack) {
    int nArguments = nArguments();
    var arguments = new Expression[nArguments];
    for (int iArgument = 0; iArgument < nArguments; iArgument++) {
      if (stack instanceof Argument argument) {
        arguments[iArgument] = argument.expression;
        stack = argument.previous;
      } else {
        throw cannotCompute();
      }
    }
    return Optional.of(computation(compute(arguments), stack));
  }

  private int nArguments() {
    return switch (this) {
      case I -> 1;
      case K -> 2;
      case S, C, B -> 3;
    };
  }

  private Expression compute(Expression[] arguments) {
    var x = arguments[0];
    var y = arguments.length >= 2 ? arguments[1] : null;
    var z = arguments.length >= 3 ? arguments[2] : null;
    return switch (this) {
      case I -> x;
      case K -> x;
      case S -> application(application(x, z), application(y, z));
      case C -> application(application(x, z), y);
      case B -> application(x, application(y, z));
      default -> throw cannotCompute();
    };
  }
}
