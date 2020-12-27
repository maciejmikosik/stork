package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class LambdaCompiler implements Compiler<Lambda> {
  public Compiler<Void> whitespace;
  public Compiler<String> alphanumeric;
  public Compiler<Expression> body;

  public Lambda compile(Input input) {
    check(input.read() == '(');
    whitespace.compile(input);
    Parameter parameter = parameter(alphanumeric.compile(input));
    whitespace.compile(input);
    check(input.read() == ')');
    whitespace.compile(input);
    return lambda(parameter, bind(parameter, body.compile(input)));
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    if (expression instanceof Variable) {
      return bind(parameter, (Variable) expression);
    } else if (expression instanceof Application) {
      return bind(parameter, (Application) expression);
    } else if (expression instanceof Lambda) {
      return bind(parameter, (Lambda) expression);
    } else {
      return expression;
    }
  }

  private static Expression bind(Parameter parameter, Variable variable) {
    return variable.name.equals(parameter.name)
        ? parameter
        : variable;
  }

  private static Expression bind(Parameter parameter, Application application) {
    return application(
        bind(parameter, application.function),
        bind(parameter, application.argument));
  }

  // TODO test shadowing
  private static Expression bind(Parameter parameter, Lambda lambda) {
    return lambda.parameter.name.equals(parameter.name)
        ? lambda
        : lambda(
            lambda.parameter,
            bind(parameter, lambda.body));
  }
}
