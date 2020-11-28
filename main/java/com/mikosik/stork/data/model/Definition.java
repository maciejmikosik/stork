package com.mikosik.stork.data.model;

public class Definition {
  public final Variable variable;
  public final Expression expression;

  private Definition(Variable variable, Expression expression) {
    this.variable = variable;
    this.expression = expression;
  }

  public static Definition definition(Variable variable, Expression expression) {
    return new Definition(variable, expression);
  }
}
