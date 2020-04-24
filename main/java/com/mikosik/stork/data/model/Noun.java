package com.mikosik.stork.data.model;

public class Noun implements Expression {
  public final Object object;

  private Noun(Object object) {
    this.object = object;
  }

  public static Expression noun(Object object) {
    return new Noun(object);
  }

  public String toString() {
    return object.toString();
  }
}
