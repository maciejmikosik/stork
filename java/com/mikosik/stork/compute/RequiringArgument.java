package com.mikosik.stork.compute;

public class RequiringArgument implements Computer {
  private final Computer computer;

  private RequiringArgument(Computer computer) {
    this.computer = computer;
  }

  public static Computer requiringArgument(Computer computer) {
    return new RequiringArgument(computer);
  }

  public Computation compute(Computation computation) {
    return computation.stack.hasArgument()
        ? computer.compute(computation)
        : computation;
  }
}
