package com.mikosik.stork.data.model;

public class Parameter implements Expression {
  public final String name;

  private Parameter(String name) {
    this.name = name;
  }

  public static Parameter parameter(String name) {
    return new Parameter(name);
  }
}
