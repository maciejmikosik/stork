package com.mikosik.lang.tool;

import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Lambda.lambda;
import static com.mikosik.lang.model.runtime.Visitor.visit;

import com.mikosik.lang.model.def.Library;
import com.mikosik.lang.model.runtime.Application;
import com.mikosik.lang.model.runtime.Core;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.runtime.Lambda;
import com.mikosik.lang.model.runtime.Primitive;
import com.mikosik.lang.model.runtime.Variable;
import com.mikosik.lang.model.runtime.Visitor;

public class Runner {
  private final Library library;

  private Runner(Library library) {
    this.library = library;
  }

  public static Runner runner(Library library) {
    return new Runner(library);
  }

  public Expression run(Expression expression) {
    return visit(expression, new Visitor<Expression>() {
      protected Expression visit(Variable variable) {
        return run(library.get(variable.name));
      }

      protected Expression visit(Primitive primitive) {
        return primitive;
      }

      protected Expression visit(Application application) {
        return run(application);
      }

      protected Expression visit(Lambda lambda) {
        return lambda;
      }

      protected Expression visit(Core core) {
        return core;
      }
    });
  }

  private Expression run(Application application) {
    Expression function = run(application.function);
    return visit(function, new Visitor<Expression>() {
      protected Expression visit(Lambda lambda) {
        return run(bind(
            lambda.body,
            lambda.parameter,
            application.argument));
      }

      protected Expression visit(Core core) {
        return core.run(run(application.argument));
      }

      protected Expression visitDefault(Expression expression) {
        // TODO fail if unknown function
        return application;
      }
    });
  }

  private Expression bind(Expression body, String parameter, Expression argument) {
    return visit(body, new Visitor<Expression>() {
      protected Expression visit(Application application) {
        return application(
            bind(application.function, parameter, argument),
            bind(application.argument, parameter, argument));
      }

      protected Expression visit(Variable variable) {
        return variable.name.equals(parameter)
            ? argument
            : variable;
      }

      protected Expression visit(Lambda lambda) {
        // TODO test shadowing
        boolean isShadowing = lambda.parameter.equals(parameter);
        return isShadowing
            ? body
            : lambda(lambda.parameter, bind(lambda.body, parameter, argument));
      }

      protected Expression visit(Primitive primitive) {
        return primitive;
      }
    });
  }
}
