package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;

public class CachingComputer implements Computer {
  private final Map<Stack, Result> original = new WeakHashMap<>();
  private final Map<Expression, Result> computed = new WeakHashMap<>();

  private final Computer computer;

  private CachingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer caching(Computer computer) {
    return new CachingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computed.containsKey(computation.expression)
        ? computation(computed.get(computation.expression).get(), computation.stack)
        : cacheAndCompute(computation);
  }

  private Computation cacheAndCompute(Computation computation) {
    cache(computation.expression, computation.stack);
    return computer.compute(computation);
  }

  private void cache(Expression expression, Stack stack) {
    if (original.containsKey(stack)) {
      Result result = original.get(stack);
      computed.put(result.get(), result);
      result.set(expression);
    } else {
      original.put(stack, new Result().set(expression));
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
