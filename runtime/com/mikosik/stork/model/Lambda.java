package com.mikosik.stork.model;

public class Lambda implements Expression {
  public final Parameter parameter;
  public final Expression body;

  private Lambda(Parameter parameter, Expression body) {
    this.parameter = parameter;
    this.body = body;
  }

  public static Lambda lambda(Parameter parameter, Expression body) {
    return new Lambda(parameter, body);
  }
}
