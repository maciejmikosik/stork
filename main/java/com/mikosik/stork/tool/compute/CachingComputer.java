package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;

public class CachingComputer implements Computer {
  private final Map<Stack, Expression> initial = new WeakHashMap<>();
  private final Map<Expression, Expression> cached = new WeakHashMap<>();

  private final Computer computer;

  private CachingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer caching(Computer computer) {
    return new CachingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return cached.containsKey(computation.expression)
        ? computation(cached.get(computation.expression), computation.stack)
        : computeAndCache(computation);
  }

  private Computation computeAndCache(Computation computation) {
    if (initial.containsKey(computation.stack)) {
      cached.put(
          initial.get(computation.stack),
          computation.expression);
    } else {
      initial.put(computation.stack, computation.expression);
    }
    return computer.compute(computation);
  }
}
