package com.mikosik.stork.model;

import static com.mikosik.stork.common.Check.check;

public class Variable implements Expression {
  public final String name;

  private Variable(String name) {
    this.name = name;
  }

  public static Variable variable(String name) {
    return new Variable(name);
  }

  public boolean isGlobal() {
    return name.contains(".");
  }

  public Variable toLocal() {
    check(isGlobal());
    return variable(name.substring(name.lastIndexOf('.') + 1));
  }
}
