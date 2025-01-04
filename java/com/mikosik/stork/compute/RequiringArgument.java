package com.mikosik.stork.compute;

import com.mikosik.stork.compute.Stack.Argument;

public class RequiringArgument implements Computer {
  private final Computer computer;

  private RequiringArgument(Computer computer) {
    this.computer = computer;
  }

  public static Computer requiringArgument(Computer computer) {
    return new RequiringArgument(computer);
  }

  public Computation compute(Computation computation) {
    return switch (computation.stack) {
      case Argument argument -> computer.compute(computation);
      default -> computation;
    };
  }
}
