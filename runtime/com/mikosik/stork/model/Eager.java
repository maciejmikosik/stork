package com.mikosik.stork.model;

public class Eager implements Expression {
  public final Expression function;

  private Eager(Expression function) {
    this.function = function;
  }

  public static Expression eager(Expression function) {
    return new Eager(function);
  }
}
