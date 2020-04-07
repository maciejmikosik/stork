package com.mikosik.lang.model.def;

import com.mikosik.lang.model.runtime.Expression;

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
