package com.mikosik.stork.common;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Logic {
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return item -> !predicate.test(item);
  }

  public static <A, B, C> BiFunction<B, A, C> flip(BiFunction<A, B, C> function) {
    return (a, b) -> function.apply(b, a);
  }

  public static <E> Function<Object, E> constant(E value) {
    return argument -> value;
  }

  public static <E> Supplier<E> singleton(Supplier<E> supplier) {
    return new Supplier<E>() {
      @SuppressWarnings("unchecked")
      private final E UNINITIALIZED = (E) new Object();
      private final AtomicReference<E> instance = new AtomicReference<E>(UNINITIALIZED);

      public E get() {
        return instance.updateAndGet(value -> value == UNINITIALIZED
            ? supplier.get()
            : value);
      }
    };
  }
}
