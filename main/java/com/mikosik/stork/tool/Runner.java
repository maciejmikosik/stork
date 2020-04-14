package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.ExpressionSwitcher.expressionSwitcherReturning;
import static com.mikosik.stork.data.model.Lambda.lambda;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Parameter;

public class Runner {
  private final Binary binary;

  private Runner(Binary binary) {
    this.binary = binary;
  }

  public static Runner runner(Binary binary) {
    return new Runner(binary);
  }

  public Expression run(Expression expression) {
    return expressionSwitcherReturning(Expression.class)
        .ifVariable(variable -> run(binary.table.get(variable.name)))
        .ifPrimitive(primitive -> primitive)
        .ifApplication(application -> run(application))
        .ifLambda(lambda -> lambda)
        .ifCore(core -> core)
        .apply(expression);
  }

  private Expression run(Application application) {
    Expression function = run(application.function);
    return expressionSwitcherReturning(Expression.class)
        .ifLambda(lambda -> run(bind(
            lambda.body,
            lambda.parameter,
            application.argument)))
        .ifCore(core -> run(core.run(run(application.argument))))
        .apply(function);
  }

  private Expression bind(Expression expression, Parameter parameter, Expression argument) {
    return expressionSwitcherReturning(Expression.class)
        .ifApplication(application -> application(
            bind(application.function, parameter, argument),
            bind(application.argument, parameter, argument)))
        .ifVariable(variable -> variable)
        .ifLambda(lambda -> lambda.parameter == parameter
            ? expression // TODO test shadowing
            : lambda(
                lambda.parameter,
                bind(lambda.body, parameter, argument)))
        .ifParameter(foundParameter -> foundParameter == parameter
            ? argument
            : foundParameter)
        .ifPrimitive(primitive -> primitive)
        .apply(expression);
  }
}
