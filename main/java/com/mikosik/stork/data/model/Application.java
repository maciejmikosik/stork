package com.mikosik.stork.data.model;

import static java.lang.String.format;

public class Application implements Expression {
  public final Expression function;
  public final Expression argument;

  private Application(Expression function, Expression argument) {
    this.function = function;
    this.argument = argument;
  }

  public static Expression application(Expression function, Expression argument) {
    return new Application(function, argument);
  }

  public static Expression application(Expression function, Expression... arguments) {
    for (Expression argument : arguments) {
      function = application(function, argument);
    }
    return function;
  }

  public String toString() {
    return format("%s(%s)", function, argument);
  }
}
