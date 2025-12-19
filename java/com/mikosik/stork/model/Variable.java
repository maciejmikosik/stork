package com.mikosik.stork.model;

public class Variable implements Expression {
  public final String name;

  private Variable(String name) {
    this.name = name;
  }

  public static Variable variable(String name) {
    return new Variable(name);
  }

  public boolean equals(Object object) {
    return object instanceof Variable variable
        && equals(variable);
  }

  private boolean equals(Variable that) {
    return name.equals(that.name);
  }

  public int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    return "variable(%s)".formatted(name);
  }
}
