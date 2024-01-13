package com.mikosik.stork.model;

public class Unit {
  public final Namespace namespace;
  public final Module module;
  public final Linkage linkage;

  private Unit(Namespace namespace, Module module, Linkage linkage) {
    this.namespace = namespace;
    this.module = module;
    this.linkage = linkage;
  }

  public static Unit unit(Namespace namespace, Module module, Linkage linkage) {
    return new Unit(namespace, module, linkage);
  }
}
