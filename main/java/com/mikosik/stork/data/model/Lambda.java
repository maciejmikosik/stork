package com.mikosik.stork.data.model;

import static java.lang.String.format;

public class Lambda implements Expression {
  public final Parameter parameter;
  public final Expression body;

  private Lambda(Parameter parameter, Expression body) {
    this.parameter = parameter;
    this.body = body;
  }

  public static Expression lambda(Parameter parameter, Expression body) {
    return new Lambda(parameter, body);
  }

  public String toString() {
    return body instanceof Lambda
        ? format("(%s)%s", parameter, body)
        : format("(%s){%s}", parameter, body);
  }
}
