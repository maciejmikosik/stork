package com.mikosik.lang.compile;

import java.util.List;

public class Unit {
  public final List<Object> children;

  private Unit(List<Object> children) {
    this.children = children;
  }

  public static Unit unit(List<Object> children) {
    return new Unit(children);
  }
}
