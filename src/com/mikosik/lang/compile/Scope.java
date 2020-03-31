package com.mikosik.lang.compile;

import java.util.List;

public class Scope {
  public final int iOpening;
  public final int iClosing;
  public final List<Scope> children;

  private Scope(int iOpening, int iClosing, List<Scope> children) {
    this.iOpening = iOpening;
    this.iClosing = iClosing;
    this.children = children;
  }

  public static Scope scope(int iOpening, int iClosing, List<Scope> children) {
    return new Scope(iOpening, iClosing, children);
  }
}
