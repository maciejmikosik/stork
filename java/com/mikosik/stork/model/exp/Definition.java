package com.mikosik.stork.model.exp;

public final class Definition implements Phrase {
  public final Reference reference;
  public final Expression body;

  private Definition(Reference reference, Expression body) {
    this.reference = reference;
    this.body = body;
  }

  public static Definition definition(Reference reference, Expression body) {
    return new Definition(reference, body);
  }
}
