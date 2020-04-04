package com.mikosik.lang.model.runtime;

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

  public String toString() {
    return format("%s(%s)", function, argument);
  }
}
