package com.mikosik.stork.common;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Collections {
  public static void checkSuchElement(boolean condition) {
    if (!condition) {
      throw new NoSuchElementException();
    }
  }

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

  public static <T, R> Collector<T, List<T>, R> toLinkedListThen(
      Function<List<T>, R> finisher) {
    return new Collector<T, List<T>, R>() {
      public Supplier<List<T>> supplier() {
        return LinkedList<T>::new;
      }

      public BiConsumer<List<T>, T> accumulator() {
        return (list, element) -> list.add(element);
      }

      public BinaryOperator<List<T>> combiner() {
        return (listA, listB) -> {
          listA.addAll(listB);
          return listA;
        };
      }

      public Function<List<T>, R> finisher() {
        return finisher;
      }

      public Set<Characteristics> characteristics() {
        return emptySet();
      }
    };
  }

  public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMapFromEntries() {
    return toMap(Entry::getKey, Entry::getValue);
  }
}
