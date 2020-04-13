package com.mikosik.stork.data.model;

import com.mikosik.stork.common.Chain;

public class Library {
  public final Chain<Definition> definitions;

  private Library(Chain<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Library library(Chain<Definition> definitions) {
    return new Library(definitions);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Definition definition : definitions) {
      builder.append(definition).append("\n\n");
    }
    return builder.toString();
  }
}
