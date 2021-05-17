package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;

public class CachingComputer implements Computer {
  private final Map<Stack, Expression> original = new WeakHashMap<>();
  private final Map<Expression, Expression> computed = new WeakHashMap<>();

  private final Computer computer;

  private CachingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer caching(Computer computer) {
    return new CachingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computed.containsKey(computation.expression)
        ? computation(computed.get(computation.expression), computation.stack)
        : cacheAndCompute(computation);
  }

  private Computation cacheAndCompute(Computation computation) {
    cache(computation.expression, computation.stack);
    return computer.compute(computation);
  }

  private void cache(Expression expression, Stack stack) {
    if (original.containsKey(stack)) {
      computed.put(original.get(stack), expression);
    } else {
      original.put(stack, expression);
    }
  }
}
