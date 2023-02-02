package com.mikosik.stork.model;

public class Identifier implements Expression {
  public final String name;

  private Identifier(String name) {
    this.name = name;
  }

  public static Identifier identifier(String name) {
    return new Identifier(name);
  }

  public Identifier toLocal() {
    return identifier(name.substring(name.lastIndexOf('.') + 1));
  }
}
