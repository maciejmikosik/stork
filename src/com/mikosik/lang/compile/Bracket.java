package com.mikosik.lang.compile;

import java.util.List;

public class Bracket {
  public final List<Object> children;

  private Bracket(List<Object> children) {
    this.children = children;
  }

  public static Bracket bracket(List<Object> children) {
    return new Bracket(children);
  }
}
