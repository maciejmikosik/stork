package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Visitor.visit;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Core;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Primitive;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.Visitor;

public class Runner {
  private final Runtime runtime;

  private Runner(Runtime runtime) {
    this.runtime = runtime;
  }

  public static Runner runner(Runtime runtime) {
    return new Runner(runtime);
  }

  public Expression run(Expression expression) {
    return visit(expression, new Visitor<Expression>() {
      protected Expression visit(Variable variable) {
        return run(runtime.find(variable.name));
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
        return core.run(application.argument, Runner.this);
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
