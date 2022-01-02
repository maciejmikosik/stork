package com.mikosik.stork.model;

import static com.mikosik.stork.model.Variable.variable;

public class Identifier implements Expression {
  public final String name;

  private Identifier(String name) {
    this.name = name;
  }

  public static Identifier identifier(String name) {
    return new Identifier(name);
  }

  public Variable toVariable() {
    return variable(name.substring(name.lastIndexOf('.') + 1));
  }
}
