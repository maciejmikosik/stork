package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Lambda.lambda;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Parameter;

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
                substitute(
                    (Lambda) computation.expression,
                    computation.stack.argument()),
                computation.stack.pop())
            : computer.compute(computation);
  }

  private static Expression substitute(Lambda lambda, Expression argument) {
    return substitute(lambda.body, lambda.parameter, argument);
  }

  private static Expression substitute(
      Expression expression,
      Parameter parameter,
      Expression argument) {
    if (expression instanceof Application) {
      return substitute((Application) expression, parameter, argument);
    } else if (expression instanceof Lambda) {
      return substitute((Lambda) expression, parameter, argument);
    } else if (expression instanceof Parameter) {
      return expression == parameter
          ? argument
          : expression;
    } else {
      return expression;
    }
  }

  private static Expression substitute(
      Application application,
      Parameter parameter,
      Expression argument) {
    return application(
        substitute(application.function, parameter, argument),
        substitute(application.argument, parameter, argument));
  }

  private static Expression substitute(
      Lambda lambda,
      Parameter parameter,
      Expression argument) {
    return lambda.parameter == parameter
        ? lambda // TODO test shadowing
        : lambda(
            lambda.parameter,
            substitute(lambda.body, parameter, argument));
  }
}
