package com.mikosik.lang.run;

import static com.mikosik.lang.model.Application.application;
import static com.mikosik.lang.model.Lambda.lambda;

import com.mikosik.lang.model.Alias;
import com.mikosik.lang.model.Application;
import com.mikosik.lang.model.Expression;
import com.mikosik.lang.model.Lambda;
import com.mikosik.lang.model.Library;
import com.mikosik.lang.model.Primitive;

public class Runner {
  private final Library library;

  private Runner(Library library) {
    this.library = library;
  }

  public static Runner runner(Library library) {
    return new Runner(library);
  }

  public Expression run(Expression expression) {
    if (expression instanceof Alias) {
      return run((Alias) expression);
    } else if (expression instanceof Primitive) {
      return expression;
    } else if (expression instanceof Application) {
      return run((Application) expression);
    } else {
      // TODO fail if unknown expression
      return expression;
    }
  }

  private Expression run(Alias alias) {
    return run(library.get(alias.name));
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
    } else if (body instanceof Alias) {
      Alias alias = (Alias) body;
      return alias.name.equals(parameter)
          ? argument
          : alias;
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
