package com.mikosik.stork.common;

import static java.util.stream.Collectors.toCollection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Chain<E> implements Iterable<E> {
  private static Chain<Object> EMPTY = new Chain<Object>(null, null);

  private final E head;
  private final Chain<E> tail;

  private Chain(E head, Chain<E> tail) {
    this.head = head;
    this.tail = tail;
  }

  public static <E> Chain<E> empty() {
    return (Chain<E>) EMPTY;
  }

  public boolean isEmpty() {
    return this == EMPTY;
  }

  private void checkNotEmpty() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
  }

  public Chain<E> add(E newHead) {
    return new Chain<E>(newHead, this);
  }

  public Chain<E> addAll(Iterator<E> iterator) {
    var result = this;
    while (iterator.hasNext()) {
      result = result.add(iterator.next());
    }
    return result;
  }

  public Chain<E> addAll(Stream<E> elements) {
    return addAll(elements.iterator());
  }

  public Chain<E> addAll(Iterable<E> elements) {
    return addAll(elements.iterator());
  }

  public Chain<E> addAll(E[] elements) {
    return addAll(Arrays.stream(elements));
  }

  public static <E> Chain<E> chainOf(E element) {
    return Chain.<E> empty()
        .add(element);
  }

  public static <E> Chain<E> chainOf(E... elements) {
    return Chain.<E> empty()
        .addAll(elements)
        .reverse();
  }

  public static <E> Chain<E> chainFrom(Iterator<E> elements) {
    return Chain.<E> empty()
        .addAll(elements)
        .reverse();
  }

  public static <E> Chain<E> chainFrom(Stream<E> elements) {
    return Chain.<E> empty()
        .addAll(elements)
        .reverse();
  }

  public static <E> Chain<E> chainFrom(Iterable<E> elements) {
    return Chain.<E> empty()
        .addAll(elements)
        .reverse();
  }

  public E head() {
    checkNotEmpty();
    return head;
  }

  public Chain<E> tail() {
    checkNotEmpty();
    return tail;
  }

  public Chain<E> reverse() {
    Chain<E> chain = empty();
    return chain.addAll(this);
  }

  public Chain<E> limit(int n) {
    return chainFrom(stream().limit(n));
  }

  public Chain<E> until(Predicate<E> separator) {
    Chain<E> chain = this;
    Chain<E> taken = empty();
    while (!chain.isEmpty()) {
      E head = chain.head();
      taken = taken.add(head);
      chain = chain.tail();
      if (separator.test(head)) {
        break;
      }
    }
    return taken.reverse();
  }

  public Chain<E> after(Predicate<E> separator) {
    Chain<E> chain = this;
    while (!chain.isEmpty()) {
      E head = chain.head();
      chain = chain.tail();
      if (separator.test(head)) {
        break;
      }
    }
    return chain;
  }

  public <R> R visit(
      BiFunction<E, Chain<E>, R> handleNotEmpty,
      Supplier<R> handleEmpty) {
    return isEmpty()
        ? handleEmpty.get()
        : handleNotEmpty.apply(head(), tail());
  }

  public <X> Chain<X> map(Function<E, X> mapper) {
    Chain<E> original = this;
    Chain<X> mapped = empty();
    while (!original.isEmpty()) {
      mapped = mapped.add(mapper.apply(original.head()));
      original = original.tail();
    }
    return mapped.reverse();
  }

  public <X> Chain<X> flatMap(Function<E, Chain<X>> mapper) {
    Chain<E> original = this;
    Chain<X> mapped = empty();
    while (!original.isEmpty()) {
      mapped = mapped.addAll(mapper.apply(original.head()));
      original = original.tail();
    }
    return mapped.reverse();
  }

  public Stream<E> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Chain<E> chain = Chain.this;

      public boolean hasNext() {
        return !chain.isEmpty();
      }

      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        E next = chain.head();
        chain = chain.tail();
        return next;
      }
    };
  }

  public List<E> toLinkedList() {
    return stream().collect(toCollection(LinkedList::new));
  }

  public HashSet<E> toHashSet() {
    return stream().collect(toCollection(HashSet::new));
  }

  public <K, V> HashMap<K, V> toHashMap(
      Function<E, K> keyExtractor,
      Function<E, V> valueExtractor) {
    HashMap<K, V> hashMap = new HashMap<K, V>();
    forEach(element -> hashMap.put(
        keyExtractor.apply(element),
        valueExtractor.apply(element)));
    return hashMap;
  }

  public boolean equals(Object obj) {
    return obj instanceof Chain && equals((Chain<?>) obj);
  }

  public boolean equals(Chain<?> that) {
    Chain<?> first = this;
    Chain<?> second = that;
    while (!first.isEmpty() && !second.isEmpty()) {
      if (!Objects.equals(first.head, second.head)) {
        return false;
      }
      first = first.tail;
      second = second.tail;
    }
    return first == second;
  }

  public int hashCode() {
    return Objects.hash(toLinkedList());
  }

  public String toString() {
    return toLinkedList().toString();
  }
}
