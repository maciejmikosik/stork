package com.mikosik.stork.common;

import static java.util.Collections.unmodifiableSet;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Collections {
  public static <E> Stream<E> stream(Iterator<E> iterator) {
    boolean parallel = false;
    return StreamSupport.stream(
        spliteratorUnknownSize(iterator, ORDERED),
        parallel);
  }

  public static <E> Stream<E> stream(Iterable<E> iterable) {
    return stream(iterable.spliterator());
  }

  private static <E> Stream<E> stream(Spliterator<E> spliterator) {
    return StreamSupport.stream(spliterator, false);
  }

  public static <A, B> Function<A, Stream<B>> filter(Class<B> clazz) {
    return element -> clazz.isInstance(element)
        ? Stream.of(clazz.cast(element))
        : Stream.empty();
  }

  public static <E> Set<E> intersection(Set<E> a, Set<E> b) {
    var result = new HashSet<>(a);
    result.retainAll(b);
    return unmodifiableSet(result);
  }

  public static <E> List<E> flatten(Iterable<? extends Iterable<E>> iterables) {
    return stream(iterables)
        .flatMap(Collections::stream)
        .toList();
  }

  public static <E> List<E> immutable(List<? extends E> original) {
    return java.util.Collections.unmodifiableList(new ArrayList<>(original));
  }
}
