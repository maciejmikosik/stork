package com.mikosik.stork.common;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Chain<E> implements Iterable<E> {
  private final E head;
  private final Chain<E> tail;

  private Chain(E head, Chain<E> tail) {
    this.head = head;
    this.tail = tail;
  }

  public static <E> Chain<E> chain() {
    return (Chain<E>) EMPTY;
  }

  public boolean available() {
    return true;
  }

  public E head() {
    return head;
  }

  public Chain<E> tail() {
    return tail;
  }

  public Chain<E> add(E element) {
    return new Chain<>(element, this);
  }

  public Chain<E> reverse() {
    Chain<E> source = this;
    Chain<E> target = chain();
    while (source.available()) {
      target = target.add(source.head);
      source = source.tail;
    }
    return target;
  }

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Chain<E> chain = Chain.this;

      public boolean hasNext() {
        return chain.available();
      }

      public E next() {
        E next = chain.head;
        chain = chain.tail;
        return next;
      }
    };
  }

  public Stream<E> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  public static <E> Chain<E> chainOf(E element) {
    Chain<E> chain = chain();
    return chain.add(element);
  }

  public static <E> Chain<E> chainOf(E... elements) {
    Chain<E> chain = chain();
    for (int i = elements.length - 1; i >= 0; i--) {
      chain = chain.add(elements[i]);
    }
    return chain;
  }

  public static <E> Chain<E> chainFrom(Iterable<E> iterable) {
    Iterator<E> iterator = iterable.iterator();
    Chain<E> chain = chain();
    while (iterator.hasNext()) {
      chain = chain.add(iterator.next());
    }
    return chain.reverse();
  }

  private static Chain<Object> EMPTY = new Chain<Object>(null, null) {
    public boolean available() {
      return false;
    }

    public Object head() {
      throw new NoSuchElementException();
    }

    public Chain<Object> tail() {
      throw new NoSuchElementException();
    }
  };
}
