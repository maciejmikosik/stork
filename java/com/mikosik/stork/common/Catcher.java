package com.mikosik.stork.common;

import static com.mikosik.stork.common.Sequence.asSequence;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Catcher<E extends RuntimeException> {
  private final Class<E> type;
  private final List<E> exceptions = new LinkedList<>();

  private Catcher(Class<E> type) {
    this.type = type;
  }

  public static <E extends RuntimeException> Catcher<E> catcher(Class<E> type) {
    return new Catcher<E>(type);
  }

  public <T> T tryCatch(Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (RuntimeException exception) {
      if (type.isInstance(exception)) {
        exceptions.add(type.cast(exception));
        return null;
      } else {
        throw exception;
      }
    }
  }

  public void rethrow(Function<Sequence<E>, RuntimeException> aggregator) {
    if (!exceptions.isEmpty()) {
      throw aggregator.apply(asSequence(exceptions));
    }
  }
}
