package com.mikosik.stork.common;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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

  public static <E> Function<E, E> untilIdempotent(Function<E, E> function) {
    return new Function<E, E>() {
      public E apply(E current) {
        var next = function.apply(current);
        while (next != current) {
          current = next;
          next = function.apply(current);
        }
        return next;
      }
    };
  }
}
