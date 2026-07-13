package com.mikosik.stork.common;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Singleton {
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
