package com.mikosik.stork.common;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImmutableList {
  private ImmutableList() {}

  public static <E> List<E> none() {
    return emptyList();
  }

  public static <E> List<E> single(E element) {
    return singletonList(element);
  }

  @SafeVarargs
  public static <E> List<E> list(E first, E second, E... rest) {
    var list = new ArrayList<E>(rest.length + 2);
    list.add(first);
    list.add(second);
    list.addAll(asList(rest));
    return unmodifiableList(list);
  }

  public static <E> List<E> toList(E[] elements) {
    return unmodifiableList(asList(elements));
  }

  public static <E> List<E> listFrom(Iterator<E> iterator) {
    return Collections.stream(iterator).toList();
  }

  @SafeVarargs
  public static <E> List<E> join(
      List<? extends E> first,
      List<? extends E> second,
      List<? extends E>... rest) {
    var joined = new ArrayList<E>();
    joined.addAll(first);
    joined.addAll(second);
    for (var list : rest) {
      joined.addAll(list);
    }
    return unmodifiableList(joined);
  }
}
