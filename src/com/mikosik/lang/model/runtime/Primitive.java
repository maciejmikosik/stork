package com.mikosik.lang.model.runtime;

public class Primitive implements Expression {
  public final Object object;

  private Primitive(Object object) {
    this.object = object;
  }

  public static Expression primitive(Object object) {
    return new Primitive(object);
  }
}
