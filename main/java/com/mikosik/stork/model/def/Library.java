package com.mikosik.stork.model.def;

import com.mikosik.stork.common.Chain;

public class Library {
  public final Chain<Definition> definitions;

  private Library(Chain<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Library library(Chain<Definition> definitions) {
    return new Library(definitions);
  }
}
