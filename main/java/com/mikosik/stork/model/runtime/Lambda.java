package com.mikosik.stork.model.runtime;

import static java.lang.String.format;

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

  public String toString() {
    return body instanceof Lambda
        ? format("(%s)%s", parameter, body)
        : format("(%s){%s}", parameter, body);
  }
}