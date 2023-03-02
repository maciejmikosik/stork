package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

public class ReturningComputer implements Computer {
  private ReturningComputer() {}

  public static Computer returningComputer() {
    return new ReturningComputer();
  }

  public Computation compute(Computation computation) {
    Stack stack = computation.stack;
    return stack.hasFunction()
        ? computation(
            application(stack.function(), computation.expression),
            stack.pop())
        : computation;
  }
}
