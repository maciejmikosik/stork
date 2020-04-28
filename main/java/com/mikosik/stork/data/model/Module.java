package com.mikosik.stork.data.model;

import com.mikosik.stork.common.Chain;

public class Module {
  public final Chain<Definition> definitions;

  private Module(Chain<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Module module(Chain<Definition> definitions) {
    return new Module(definitions);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Definition definition : definitions) {
      builder.append(definition).append("\n\n");
    }
    return builder.toString();
  }
}
