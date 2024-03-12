package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Slot.slot;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.UncloningComputer.uncloning;

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
    return uncloning(new CachingComputer(computer));
  }

  public Computation compute(Computation computation) {
    if (cacheHas(computation)) {
      return fromCache(computation);
    } else {
      toCache(computation);
      return computer.compute(computation);
    }
  }

  private boolean cacheHas(Computation computation) {
    return cachedExpressions.containsKey(computation.expression);
  }

  private Computation fromCache(Computation computation) {
    var cachedExpression = cachedExpressions.get(computation.expression).value;
    return computation(
        cachedExpression,
        computation.stack);
  }

  private void toCache(Computation computation) {
    if (cachedStacks.containsKey(computation.stack)) {
      var slot = cachedStacks.get(computation.stack);
      cachedExpressions.put(slot.value, slot);
      slot.value = computation.expression;
    } else {
      cachedStacks.put(computation.stack, slot(computation.expression));
    }
  }
}
