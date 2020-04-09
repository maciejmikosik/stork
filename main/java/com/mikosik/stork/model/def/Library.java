package com.mikosik.stork.model.def;

import static com.mikosik.stork.model.def.Definition.definition;

import java.util.List;

import com.mikosik.stork.model.runtime.Expression;

public class Library {
  // TODO make immutable
  public final List<Definition> definitions;

  private Library(List<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Library library(List<Definition> definitions) {
    return new Library(definitions);
  }

  public Library define(String name, Expression expression) {
    definitions.add(definition(name, expression));
    return this;
  }
}
