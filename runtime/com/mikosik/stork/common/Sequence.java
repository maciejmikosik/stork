package com.mikosik.stork.common;

import static com.mikosik.stork.common.Collections.stream;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

public class Sequence {
  public static <E> List<E> sequence(List<E> list) {
    return unmodifiableList(list);
  }

  public static <E> List<E> sequence(E element) {
    return singletonList(element);
  }

  public static <E> List<E> sequence(E... elements) {
    return unmodifiableList(Arrays.asList(elements));
  }

  public static <E> List<E> sequence(Iterable<E> iterable) {
    return stream(iterable).collect(toUnmodifiableList());
  }

  public static <T> Collector<T, ?, List<T>> toSequence() {
    return toUnmodifiableList();
  }
}
