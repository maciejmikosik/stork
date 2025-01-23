package com.mikosik.stork.model;

public class Unit {
  public final Namespace namespace;
  public final Library library;
  public final Linkage linkage;

  private Unit(Namespace namespace, Library library, Linkage linkage) {
    this.namespace = namespace;
    this.library = library;
    this.linkage = linkage;
  }

  public static Unit unit(Namespace namespace, Library library, Linkage linkage) {
    return new Unit(namespace, library, linkage);
  }
}
