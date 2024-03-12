package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computations.areSame;

public class UncloningComputer implements Computer {
  private final Computer computer;

  private UncloningComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer uncloning(Computer computer) {
    return new UncloningComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation computed = computer.compute(computation);
    return areSame(computation, computed)
        ? computation
        : computed;
  }
}
