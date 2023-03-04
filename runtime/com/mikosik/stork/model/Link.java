package com.mikosik.stork.model;

public class Link {
  public final Variable variable;
  public final Identifier identifier;

  private Link(Variable variable, Identifier identifier) {
    this.variable = variable;
    this.identifier = identifier;
  }

  public static Link link(Identifier identifier) {
    return new Link(identifier.variable, identifier);
  }

  public static Link link(Identifier identifier, Variable variable) {
    return new Link(variable, identifier);
  }
}
