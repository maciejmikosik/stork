package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequenceOf;

import com.mikosik.stork.common.Sequence;

public class Library {
  public final Sequence<Definition> definitions;

  private Library(Sequence<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Library library(Sequence<Definition> definitions) {
    return new Library(definitions);
  }

  public static Library libraryOf(Definition... definitions) {
    return library(sequenceOf(definitions));
  }
}
