package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.comp.Computation.computation;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;

public class SubstitutingComputer implements Computer {
  private final Computer nextComputer;

  private SubstitutingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer substituting(Computer nextComputer) {
    return new SubstitutingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Lambda
        && computation.stack instanceof Argument
            ? substitute(
                (Lambda) computation.expression,
                (Argument) computation.stack)
            : nextComputer.compute(computation);
  }

  private Computation substitute(Lambda lambda, Argument argument) {
    return computation(
        substitute(lambda.body, lambda.parameter, argument.expression),
        argument.stack);
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
