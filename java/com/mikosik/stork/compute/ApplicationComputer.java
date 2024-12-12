package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;

import com.mikosik.stork.model.Application;

public class ApplicationComputer implements Computer {
  private ApplicationComputer() {}

  public static Computer applicationComputer() {
    return new ApplicationComputer();
  }

  public Computation compute(Computation computation) {
    return switch (computation.expression) {
      case Application application -> computation(
          application.function,
          computation.stack.pushArgument(application.argument));
      default -> computation;
    };
  }
}
