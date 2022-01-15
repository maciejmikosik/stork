package com.mikosik.stork.model;

public class Definition implements Model {
  public final Identifier identifier;
  public final Expression body;

  private Definition(Identifier identifier, Expression body) {
    this.identifier = identifier;
    this.body = body;
  }

  public static Definition definition(Identifier identifier, Expression body) {
    return new Definition(identifier, body);
  }
}
