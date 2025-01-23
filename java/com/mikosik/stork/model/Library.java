package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.common.Sequence.toSequenceThen;
import static java.util.Arrays.asList;

import java.util.List;

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

  public static Library join(List<Library> libraries) {
    return libraries.stream()
        .flatMap(library -> library.definitions.stream())
        .collect(toSequenceThen(Library::library));
  }

  public static Library join(Library... libraries) {
    return join(asList(libraries));
  }
}
