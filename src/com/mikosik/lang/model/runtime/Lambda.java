package com.mikosik.lang.model.runtime;

public class Lambda implements Expression {
  public final String parameter;
  public final Expression body;

  private Lambda(String parameter, Expression body) {
    this.parameter = parameter;
    this.body = body;
  }

  public static Expression lambda(String parameter, Expression body) {
    return new Lambda(parameter, body);
  }
}
