package com.mikosik.lang.model;

public class Variable implements Expression {
  public final String name;

  private Variable(String name) {
    this.name = name;
  }

  public static Expression variable(String name) {
    return new Variable(name);
  }
}
