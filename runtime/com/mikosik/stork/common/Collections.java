package com.mikosik.stork.common;

import static com.mikosik.stork.common.Sequence.toSequence;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.List;
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

  public static <E> Stream<E> stream(Iterable<? extends E> iterable) {
    return stream(iterable.spliterator());
  }

  private static <E> Stream<E> stream(Spliterator<? extends E> spliterator) {
    return (Stream<E>) StreamSupport.stream(spliterator, false);
  }

  public static <A, B> Function<A, Stream<B>> instanceOf(Class<B> clazz) {
    return element -> clazz.isInstance(element)
        ? Stream.of((B) element)
        : Stream.empty();
  }

  public static <E> List<E> flatten(Iterable<? extends Iterable<? extends E>> iterables) {
    return stream(iterables)
        .flatMap(Collections::stream)
        .collect(toSequence());
  }
}
