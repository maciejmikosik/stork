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
}
