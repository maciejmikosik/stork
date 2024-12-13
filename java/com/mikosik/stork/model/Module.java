package com.mikosik.stork.model;

import static com.mikosik.stork.common.Collections.immutable;

import java.util.List;

public class Module {
  public final List<Definition> definitions;

  private Module(List<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Module module(List<? extends Definition> definitions) {
    return new Module(immutable(definitions));
  }
}
