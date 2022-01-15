package com.mikosik.stork.model;

import com.mikosik.stork.common.Chain;

public class Module implements Model {
  public final Chain<Definition> definitions;

  private Module(Chain<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Module module(Chain<Definition> definitions) {
    return new Module(definitions);
  }
}
