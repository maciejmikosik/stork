package com.mikosik.stork.model;

public class Link {
  public final Identifier identifier;

  private Link(Identifier identifier) {
    this.identifier = identifier;
  }

  public static Link link(Identifier identifier) {
    return new Link(identifier);
  }
}
