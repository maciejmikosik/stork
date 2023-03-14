package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequence;

import java.util.List;

public class Module implements Model {
  public final List<Definition> definitions;

  private Module(List<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Module module(List<Definition> definitions) {
    return new Module(sequence(definitions));
  }
}
