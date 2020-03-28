package com.mikosik.lang.model;

public class Alias implements Expression {
  public final String name;

  private Alias(String name) {
    this.name = name;
  }

  public static Expression alias(String name) {
    return new Alias(name);
  }
}
