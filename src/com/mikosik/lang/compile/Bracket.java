package com.mikosik.lang.compile;

import java.util.List;

public class Bracket implements Syntax {
  public final List<Syntax> children;

  private Bracket(List<Syntax> children) {
    this.children = children;
  }

  public static Bracket bracket(List<Syntax> children) {
    return new Bracket(children);
  }
}
