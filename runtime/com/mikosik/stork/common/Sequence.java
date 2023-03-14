package com.mikosik.stork.common;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class Sequence {
  public static <E> List<E> sequence(List<E> list) {
    return unmodifiableList(list);
  }
}
