package com.mikosik.stork.common;

import static com.mikosik.stork.common.Collections.toLinkedListThen;
import static java.util.Arrays.asList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;

// TODO implement methods directly instead of delegating
public class Sequence<E> extends AbstractList<E> {
  private final List<E> elements;

  private Sequence(List<E> elements) {
    this.elements = elements;
  }

  @SafeVarargs
  public static <E> Sequence<E> sequenceOf(E... elements) {
    return asSequence(asList(elements));
  }

  public static <E> Sequence<E> asSequence(Collection<E> collection) {
    return new Sequence<>(new ArrayList<>(collection));
  }

  public static <E> Sequence<E> sequenceFrom(Iterator<E> iterator) {
    return Collections.stream(iterator)
        .collect(toSequence());
  }

  @SafeVarargs
  public static <E> Sequence<E> flatten(Iterable<? extends E>... iterables) {
    return Arrays.stream(iterables)
        .flatMap(Collections::stream)
        .collect(toSequence());
  }

  public int size() {
    return elements.size();
  }

  public E get(int index) {
    return elements.get(index);
  }

  public Sequence<E> subList(int fromIndex, int toIndex) {
    return new Sequence<>(elements.subList(fromIndex, toIndex));
  }

  public static <E> Collector<E, List<E>, Sequence<E>> toSequence() {
    return toLinkedListThen(Sequence::asSequence);
  }
}
