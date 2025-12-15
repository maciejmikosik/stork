package com.mikosik.stork.compute;

import com.mikosik.stork.model.Expression;

public abstract class TypedComputer<E extends Expression> implements Computer {
  private final Class<E> type;

  protected TypedComputer(Class<E> type) {
    this.type = type;
  }

  public Computation compute(Computation computation) {
    return compute(
        type.cast(computation.expression),
        computation.stack);
  }

  public abstract Computation compute(E expression, Stack stack);
}
