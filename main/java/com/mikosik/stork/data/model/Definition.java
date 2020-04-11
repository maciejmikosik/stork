package com.mikosik.stork.data.model;

public class Definition {
  public final String name;
  public final Expression expression;

  private Definition(String name, Expression expression) {
    this.name = name;
    this.expression = expression;
  }

  public static Definition definition(String name, Expression expression) {
    return new Definition(name, expression);
  }
}
