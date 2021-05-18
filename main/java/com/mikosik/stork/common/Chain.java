package com.mikosik.stork.common;

import static java.util.stream.Collectors.toCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
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

  public static <E> Chain<E> chainOf(E element) {
    Chain<E> chain = empty();
    return chain.add(element);
  }

  public static <E> Chain<E> chainOf(E... elements) {
    Chain<E> chain = empty();
    for (E element : elements) {
      chain = chain.add(element);
    }
    return chain.reverse();
  }

  public static <E> Chain<E> chainFrom(Iterable<E> iterable) {
    Iterator<E> iterator = iterable.iterator();
    Chain<E> chain = empty();
    while (iterator.hasNext()) {
      chain = chain.add(iterator.next());
    }
    return chain.reverse();
  }

  public E head() {
    checkNotEmpty();
    return head;
  }

  public Chain<E> tail() {
    checkNotEmpty();
    return tail;
  }

  public Chain<E> add(E newHead) {
    return new Chain<E>(newHead, this);
  }

  public Chain<E> addAll(Chain<E> chain) {
    Chain<E> result = this;
    while (!chain.isEmpty()) {
      result = result.add(chain.head());
      chain = chain.tail();
    }
    return result;
  }

  public Chain<E> reverse() {
    Chain<E> chain = empty();
    return chain.addAll(this);
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
}
