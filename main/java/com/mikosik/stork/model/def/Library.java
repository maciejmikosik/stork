package com.mikosik.stork.model.def;

import static com.mikosik.stork.model.def.Definition.definition;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.model.runtime.Expression;

public class Library {
  // TODO make immutable
  public final List<Definition> definitions = new LinkedList<>();

  private Library() {}

  public static Library library() {
    return new Library();
  }

  public Library define(String name, Expression expression) {
    definitions.add(definition(name, expression));
    return this;
  }

  public Library add(Definition definition) {
    definitions.add(definition);
    return this;
  }
}
