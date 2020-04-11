package com.mikosik.stork.data.model;

public class Parameter {
  public final String name;

  private Parameter(String name) {
    this.name = name;
  }

  public static Parameter parameter(String name) {
    return new Parameter(name);
  }

  public String toString() {
    return name;
  }
}
