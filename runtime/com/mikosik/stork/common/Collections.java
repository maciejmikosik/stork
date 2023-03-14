package com.mikosik.stork.common;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
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
    return StreamSupport.stream(iterable.spliterator(), false);
  }
}
