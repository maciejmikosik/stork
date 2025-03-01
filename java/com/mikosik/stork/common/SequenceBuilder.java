package com.mikosik.stork.common;

import static com.mikosik.stork.common.Sequence.asSequence;
import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

public class SequenceBuilder<E> {
  private final List<E> elements = new LinkedList<E>();

  private SequenceBuilder() {}

  @SuppressWarnings("unchecked")
  public static <E> SequenceBuilder<E> sequenceBuilderOf(E... elements) {
    return new SequenceBuilder<E>().addAll(elements);
  }

  @SuppressWarnings("unchecked")
  public SequenceBuilder<E> addAll(E... elements) {
    this.elements.addAll(asList(elements));
    return this;
  }

  @SuppressWarnings("unchecked")
  public SequenceBuilder<E> andAllIf(boolean condition, E... elements) {
    if (condition) {
      this.elements.addAll(asList(elements));
    }
    return this;
  }

  public Sequence<E> build() {
    return asSequence(elements);
  }
}
