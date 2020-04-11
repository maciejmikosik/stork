package com.mikosik.stork.data.model;

public class Primitive implements Expression {
  public final Object object;

  private Primitive(Object object) {
    this.object = object;
  }

  public static Expression primitive(Object object) {
    return new Primitive(object);
  }

  public String toString() {
    return object.toString();
  }
}
