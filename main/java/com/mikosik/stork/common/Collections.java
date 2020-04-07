package com.mikosik.stork.common;

import java.util.List;

public class Collections {
  public static <E> E first(List<E> list) {
    return list.get(0);
  }

  public static <E> List<E> skipFirst(List<E> list) {
    return list.subList(1, list.size());
  }
}
