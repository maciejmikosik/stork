package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Switch.switchOn;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;

public class Expressions {
  /**
   * When running {@link Application} that applies {@link Lambda} on given argument
   * {@link Expression} it is required to replace each occurrence of lambda's parameter in lambda's
   * body by given argument. This method performs this.
   *
   * @param expression
   *          expression to perform substitution on (e.g. lambda's body)
   * @param parameter
   *          parameter in lambda's body to be substituted by argument
   * @param argument
   *          expression to replace parameter
   * @return expression after performing substitution
   */
  public static Expression substitute(
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
        .ifPrimitive(primitive -> primitive)
        .elseFail();
  }

  public static Expression substitute(Lambda lambda, Expression argument) {
    return substitute(lambda.body, lambda.parameter, argument);
  }
}
