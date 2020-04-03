package com.mikosik.lang.compile;

import java.util.List;

public class Unit implements Syntax {
  public final List<Syntax> children;

  private Unit(List<Syntax> children) {
    this.children = children;
  }

  public static Unit unit(List<Syntax> children) {
    return new Unit(children);
  }
}
