package com.mikosik.stork.data.model;

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
}
