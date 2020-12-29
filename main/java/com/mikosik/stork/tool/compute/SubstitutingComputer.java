package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.tool.common.Traverser;

public class SubstitutingComputer implements Computer {
  private final Computer computer;

  private SubstitutingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer substituting(Computer computer) {
    return new SubstitutingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Lambda
        && computation.stack.hasArgument()
            ? computation(
                apply(
                    (Lambda) computation.expression,
                    computation.stack.argument()),
                computation.stack.pop())
            : computer.compute(computation);
  }

  private static Expression apply(Lambda lambda, Expression argument) {
    return replaceIn(lambda.body, lambda.parameter, argument);
  }

  private static Expression replaceIn(
      Expression expression,
      Parameter original,
      Expression replacement) {
    return new Traverser() {
      protected Expression traverse(Parameter parameter) {
        return parameter == original
            ? replacement
            : parameter;
      }

      protected Expression traverse(Lambda lambda) {
        return lambda.parameter == original
            ? lambda // TODO test shadowing
            : super.traverse(lambda);
      }
    }.traverse(expression);
  }
}
