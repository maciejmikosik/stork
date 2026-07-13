package com.mikosik.stork.common.func;

import java.util.function.Function;

public class On<E> {
  private final E item;

  public On(E item) {
    this.item = item;
  }

  public static <E> On<E> on(E item) {
    return new On<>(item);
  }

  public <R> On<R> map(Function<? super E, ? extends R> mapping) {
    return on(mapping.apply(item));
  }

  public E get() {
    return item;
  }
}
