package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

import com.mikosik.stork.compute.Stack.Function;

public class ReturningComputer implements Computer {
  private ReturningComputer() {}

  public static Computer returningComputer() {
    return new ReturningComputer();
  }

  public Computation compute(Computation computation) {
    return switch (computation.stack) {
      case Function function -> computation(
          application(function.expression, computation.expression),
          function.previous);
      default -> computation;
    };
  }
}
