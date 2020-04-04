package com.mikosik.lang.model.runtime;

public class Variable implements Expression {
  public final String name;

  private Variable(String name) {
    this.name = name;
  }

  public static Expression variable(String name) {
    return new Variable(name);
  }

  public String toString() {
    return name;
  }
}
