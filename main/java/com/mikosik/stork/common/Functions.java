package com.mikosik.stork.common;

import java.util.function.Predicate;

public class Functions {
  public static <T> Predicate<T> none() {
    return t -> false;
  }
}
