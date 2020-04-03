package com.mikosik.lang.run;

import static com.mikosik.lang.model.Application.application;
import static com.mikosik.lang.model.Lambda.lambda;

import com.mikosik.lang.model.Application;
import com.mikosik.lang.model.Expression;
import com.mikosik.lang.model.Lambda;
import com.mikosik.lang.model.Library;
import com.mikosik.lang.model.Primitive;
import com.mikosik.lang.model.Variable;

public class Runner {
  private final Library library;

  private Runner(Library library) {
    this.library = library;
  }

  public static Runner runner(Library library) {
    return new Runner(library);
  }

  public Expression run(Expression expression) {
    if (expression instanceof Variable) {
      return run((Variable) expression);
    } else if (expression instanceof Primitive) {
      return expression;
    } else if (expression instanceof Application) {
      return run((Application) expression);
    } else {
      // TODO fail if unknown expression
      return expression;
    }
  }

  private Expression run(Variable variable) {
    return run(library.get(variable.name));
  }

  private Expression run(Application application) {
    Expression function = run(application.function);
    if (function instanceof Lambda) {
      Lambda lambda = (Lambda) function;
      return bind(
          lambda.body,
          lambda.parameter,
          application.argument);
    } else {
      // TODO fail if unknown function
      return application;
    }
  }

  private Expression bind(Expression body, String parameter, Expression argument) {
    if (body instanceof Application) {
      Application application = (Application) body;
      return application(
          bind(application.function, parameter, argument),
          bind(application.argument, parameter, argument));
    } else if (body instanceof Variable) {
      Variable variable = (Variable) body;
      return variable.name.equals(parameter)
          ? argument
          : variable;
    } else if (body instanceof Lambda) {
      Lambda lambda = (Lambda) body;
      // TODO test shadowing
      boolean isShadowing = lambda.parameter.equals(parameter);
      return isShadowing
          ? body
          : lambda(lambda.parameter, bind(lambda.body, parameter, argument));

    } else {
      // TODO fail if unknown expression
      return body;
    }
  }
}
