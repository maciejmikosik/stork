package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Lambda.lambda;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Core;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.ExpressionVisitor;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.Primitive;
import com.mikosik.stork.data.model.Variable;

public class Runner {
  private final Runtime runtime;

  private Runner(Runtime runtime) {
    this.runtime = runtime;
  }

  public static Runner runner(Runtime runtime) {
    return new Runner(runtime);
  }

  public Expression run(Expression expression) {
    ExpressionVisitor<Expression> visitor = new ExpressionVisitor<Expression>() {
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
    };
    return visitor.visit(expression);
  }

  private Expression run(Application application) {
    Expression function = run(application.function);
    ExpressionVisitor<Expression> visitor = new ExpressionVisitor<Expression>() {
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
    };
    return visitor.visit(function);
  }

  private Expression bind(Expression body, Parameter parameter, Expression argument) {
    ExpressionVisitor<Expression> visitor = new ExpressionVisitor<Expression>() {
      protected Expression visit(Application application) {
        return application(
            bind(application.function, parameter, argument),
            bind(application.argument, parameter, argument));
      }

      protected Expression visit(Variable variable) {
        return variable.name.equals(parameter.name)
            ? argument
            : variable;
      }

      protected Expression visit(Lambda lambda) {
        // TODO test shadowing
        boolean isShadowing = lambda.parameter.name.equals(parameter.name);
        return isShadowing
            ? body
            : lambda(lambda.parameter, bind(lambda.body, parameter, argument));
      }

      protected Expression visit(Primitive primitive) {
        return primitive;
      }
    };
    return visitor.visit(body);
  }
}
