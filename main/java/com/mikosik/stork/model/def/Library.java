package com.mikosik.stork.model.def;

import java.util.List;

public class Library {
  // TODO make immutable
  public final List<Definition> definitions;

  private Library(List<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Library library(List<Definition> definitions) {
    return new Library(definitions);
  }
}
