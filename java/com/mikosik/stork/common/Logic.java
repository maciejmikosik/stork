package com.mikosik.stork.common;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mikosik.stork.common.func.Functions.Fab;
import com.mikosik.stork.common.func.Functions.Fabc;

public class Logic {
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return item -> !predicate.test(item);
  }

  public static <A, B, C> Fabc<B, A, C> flip(Fabc<A, B, C> function) {
    return (a, b) -> function.apply(b, a);
  }

  public static <E> Fab<Object, E> constant(E value) {
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
