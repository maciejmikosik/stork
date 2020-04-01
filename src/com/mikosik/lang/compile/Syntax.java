package com.mikosik.lang.compile;

import java.util.List;

public class Syntax {
  public final List<Object> children;

  private Syntax(List<Object> children) {
    this.children = children;
  }

  public static Syntax syntax(List<Object> children) {
    return new Syntax(children);
  }
}
