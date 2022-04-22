package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Stack;

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
