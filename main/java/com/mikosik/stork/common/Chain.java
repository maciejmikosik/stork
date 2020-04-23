package com.mikosik.stork.common;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Chain<E> implements Iterable<E> {
  private final E head;
  private final Chain<E> tail;

  private Chain(E head, Chain<E> tail) {
    this.head = head;
    this.tail = tail;
  }

  public static <E> Chain<E> add(E head, Chain<E> tail) {
    return new Chain<E>(head, tail);
  }

  public static <E> Chain<E> empty() {
    return (Chain<E>) EMPTY;
  }

  public <R> R visit(
      BiFunction<E, Chain<E>, R> handleNotEmpty,
      Supplier<R> handleEmpty) {
    return handleNotEmpty.apply(head, tail);
  }

  private static Chain<Object> EMPTY = new Chain<Object>(null, null) {
    public <R> R visit(
        BiFunction<Object, Chain<Object>, R> handleNotEmpty,
        Supplier<R> handleEmpty) {
      return handleEmpty.get();
    }
  };

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Chain<E> chain = Chain.this;

      public boolean hasNext() {
        return chain != EMPTY;
      }

      public E next() {
        E next = chain.head;
        chain = chain.tail;
        return next;
      }
    };
  }
}
