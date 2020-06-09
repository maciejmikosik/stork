package com.mikosik.stork.tool.common;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Switch.switchOn;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;

public class Substitute {
  /**
   * When computing {@link Application} that applies {@link Lambda} on given argument
   * {@link Expression} it is required to replace each occurrence of lambda's parameter in lambda's
   * body by given argument. This method performs this.
   */
  public static Expression substitute(Lambda lambda, Expression argument) {
    return substitute(lambda.body, lambda.parameter, argument);
  }

  private static Expression substitute(
      Expression expression,
      Parameter parameter,
      Expression argument) {
    return switchOn(expression)
        .ifApplication(application -> application(
            substitute(application.function, parameter, argument),
            substitute(application.argument, parameter, argument)))
        .ifVariable(variable -> variable)
        .ifLambda(lambda -> lambda.parameter == parameter
            ? expression // TODO test shadowing
            : lambda(
                lambda.parameter,
                substitute(lambda.body, parameter, argument)))
        .ifParameter(foundParameter -> foundParameter == parameter
            ? argument
            : foundParameter)
        .ifNoun(verb -> verb)
        .elseFail();
  }
}
