package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Chain.empty;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;

public class Invocation {
  public Variable function;
  public Chain<Expression> arguments;

  private Invocation(Variable function, Chain<Expression> arguments) {
    this.function = function;
    this.arguments = arguments;
  }

  public static Invocation invocation(
      Variable function,
      Chain<Expression> arguments) {
    return new Invocation(function, arguments);
  }

  public static Invocation asInvocation(Expression expression) {
    return asInvocation(expression, empty());
  }

  private static Invocation asInvocation(
      Expression expression,
      Chain<Expression> arguments) {
    if (expression instanceof Application) {
      Application application = (Application) expression;
      return asInvocation(
          application.function,
          arguments.add(application.argument));
    } else if (expression instanceof Variable) {
      Variable variable = (Variable) expression;
      return invocation(variable, arguments);
    } else {
      throw new RuntimeException();
    }
  }
}
