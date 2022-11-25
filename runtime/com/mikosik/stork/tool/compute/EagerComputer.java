package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Eager;
import com.mikosik.stork.model.Stack;

public class EagerComputer implements Computer {
  private EagerComputer() {}

  public static Computer eagerComputer() {
    return new EagerComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Eager
        && computation.stack.hasArgument()
            ? compute((Eager) computation.expression, computation.stack)
            : computation;
  }

  private static Computation compute(Eager eager, Stack stack) {
    return computation(
        stack.argument(),
        stack.pop().pushFunction(eager.function));
  }
}
