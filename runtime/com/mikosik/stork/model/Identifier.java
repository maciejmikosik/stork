package com.mikosik.stork.model;

public class Identifier implements Expression {
  private final String name;

  private Identifier(String name) {
    this.name = name;
  }

  public static Identifier identifier(String name) {
    return new Identifier(name);
  }

  public Identifier toLocal() {
    return identifier(name.substring(name.lastIndexOf('.') + 1));
  }

  public String name() {
    return name;
  }

  public boolean equals(Object that) {
    return that instanceof Identifier identifier
        && name.equals(identifier.name);
  }

  public int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    return name;
  }
}
