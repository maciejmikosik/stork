package com.mikosik.stork.common;

import java.util.function.IntPredicate;

public class Logic {
  public static IntPredicate not(IntPredicate predicate) {
    return item -> !predicate.test(item);
  }
}
