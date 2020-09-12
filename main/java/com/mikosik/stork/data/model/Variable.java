package com.mikosik.stork.data.model;

public class Variable implements Expression {
  public final String name;

  private Variable(String name) {
    this.name = name;
  }

  public static Variable variable(String name) {
    return new Variable(name);
  }

  public String toString() {
    return name;
  }
}
