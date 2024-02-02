package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Slot.slot;
import static com.mikosik.stork.compute.Computation.computation;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.common.Slot;
import com.mikosik.stork.model.Expression;

public class CachingComputer implements Computer {
  private final Map<Stack, Slot<Expression>> cachedStacks = new WeakHashMap<>();
  private final Map<Expression, Slot<Expression>> cachedExpressions = new WeakHashMap<>();

  private final Computer computer;

  private CachingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer caching(Computer computer) {
    return new CachingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return cachedExpressions.containsKey(computation.expression)
        ? computation(cachedExpressions.get(computation.expression).value, computation.stack)
        : cacheAndCompute(computation);
  }

  private Computation cacheAndCompute(Computation computation) {
    cache(computation.expression, computation.stack);
    return computer.compute(computation);
  }

  private void cache(Expression expression, Stack stack) {
    if (cachedStacks.containsKey(stack)) {
      var slot = cachedStacks.get(stack);
      cachedExpressions.put(slot.value, slot);
      slot.value = expression;
    } else {
      cachedStacks.put(stack, slot(expression));
    }
  }
}
