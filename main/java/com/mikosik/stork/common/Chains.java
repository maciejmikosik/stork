package com.mikosik.stork.common;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Throwables.fail;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Chains {
  public static <E> Chain<E> chainOf(E element) {
    Chain<E> chain = empty();
    return add(element, chain);
  }

  public static <E> Chain<E> chainOf(E... elements) {
    Chain<E> chain = empty();
    for (int i = elements.length - 1; i >= 0; i--) {
      chain = add(elements[i], chain);
    }
    return chain;
  }

  public static <E> Chain<E> chainFrom(Iterable<E> iterable) {
    Iterator<E> iterator = iterable.iterator();
    Chain<E> chain = empty();
    while (iterator.hasNext()) {
      chain = add(iterator.next(), chain);
    }
    return reverse(chain);
  }

  public static <E> Stream<E> stream(Chain<E> chain) {
    return StreamSupport.stream(chain.spliterator(), false);
  }

  public static <E, X> Chain<X> map(Function<E, X> mapper, Chain<E> chain) {
    Chain<X> mapped = empty();
    for (E element : chain) {
      mapped = add(mapper.apply(element), mapped);
    }
    return reverse(mapped);
  }

  public static <E> Chain<E> addAll(Chain<E> source, Chain<E> target) {
    return source.visit(
        (head, tail) -> addAll(tail, add(head, target)),
        () -> target);
  }

  public static <E> Chain<E> reverse(Chain<E> chain) {
    return addAll(chain, empty());
  }

  public static boolean isEmpty(Chain<?> chain) {
    return chain == empty();
  }

  public static <E> E head(Chain<E> chain) {
    return chain.visit(
        (head, tail) -> head,
        () -> fail(""));
  }

  public static <E> Chain<E> tail(Chain<E> chain) {
    return chain.visit(
        (head, tail) -> tail,
        () -> fail(""));
  }

  public static <E> Chain<E> takeUntil(Predicate<E> separator, Chain<E> chain) {
    Chain<E> result = empty();
    while (!isEmpty(chain)) {
      E head = head(chain);
      result = add(head, result);
      chain = tail(chain);
      if (separator.test(head)) {
        break;
      }
    }
    return reverse(result);
  }

  public static <E> Chain<E> takeAfter(Predicate<E> separator, Chain<E> chain) {
    while (!isEmpty(chain)) {
      E head = head(chain);
      chain = tail(chain);
      if (separator.test(head)) {
        break;
      }
    }
    return chain;
  }
}
