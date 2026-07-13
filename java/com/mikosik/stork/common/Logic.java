package com.mikosik.stork.common;

import java.util.function.Predicate;

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
}
