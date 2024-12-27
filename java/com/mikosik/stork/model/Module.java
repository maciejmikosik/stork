package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequenceOf;

import java.io.Serializable;

import com.mikosik.stork.common.Sequence;

// TODO rename to library
public class Module implements Serializable {
  public final Sequence<Definition> definitions;

  private Module(Sequence<Definition> definitions) {
    this.definitions = definitions;
  }

  public static Module module(Sequence<Definition> definitions) {
    return new Module(definitions);
  }

  public static Module moduleOf(Definition... definitions) {
    return module(sequenceOf(definitions));
  }
}
