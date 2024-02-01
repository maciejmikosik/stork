package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.model.Expression;

public class CachingComputer implements Computer {
  private final Map<Stack, Result> cachedStacks = new WeakHashMap<>();
  private final Map<Expression, Result> cachedExpressions = new WeakHashMap<>();

  private final Computer computer;

  private CachingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer caching(Computer computer) {
    return new CachingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return cachedExpressions.containsKey(computation.expression)
        ? computation(cachedExpressions.get(computation.expression).get(), computation.stack)
        : cacheAndCompute(computation);
  }

  private Computation cacheAndCompute(Computation computation) {
    cache(computation.expression, computation.stack);
    return computer.compute(computation);
  }

  private void cache(Expression expression, Stack stack) {
    if (cachedStacks.containsKey(stack)) {
      Result result = cachedStacks.get(stack);
      cachedExpressions.put(result.get(), result);
      result.set(expression);
    } else {
      cachedStacks.put(stack, new Result().set(expression));
    }
  }

  private static final class Result {
    private Expression expression;

    public Expression get() {
      return expression;
    }

    public Result set(Expression expression) {
      this.expression = expression;
      return this;
    }
  }
}
