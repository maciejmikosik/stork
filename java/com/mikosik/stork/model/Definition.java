package com.mikosik.stork.model;

import static com.mikosik.stork.model.Identifier.identifier;

public class Definition {
  public final Identifier identifier;
  public final Expression body;

  private Definition(Identifier identifier, Expression body) {
    this.identifier = identifier;
    this.body = body;
  }

  public static Definition definition(Identifier identifier, Expression body) {
    return new Definition(identifier, body);
  }

  public static Definition definition(String name, Expression body) {
    return definition(identifier(name), body);
  }
}
