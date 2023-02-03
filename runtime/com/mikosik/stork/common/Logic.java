package com.mikosik.stork.common;

import java.util.function.BiFunction;
import java.util.function.IntPredicate;

public class Logic {
  public static IntPredicate not(IntPredicate predicate) {
    return item -> !predicate.test(item);
  }

  public static <A, B, C> BiFunction<B, A, C> flip(BiFunction<A, B, C> function) {
    return (a, b) -> function.apply(b, a);
  }
}
