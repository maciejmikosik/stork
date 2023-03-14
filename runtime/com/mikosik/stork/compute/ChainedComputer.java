package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Sequence.sequence;

import java.util.List;

public class ChainedComputer implements Computer {
  private final List<Computer> computers;

  private ChainedComputer(List<Computer> computers) {
    this.computers = computers;
  }

  public static Computer chained(List<Computer> computers) {
    return new ChainedComputer(sequence(computers));
  }

  public Computation compute(Computation computation) {
    for (Computer computer : computers) {
      Computation computed = computer.compute(computation);
      if (computed != computation) {
        return computed;
      }
    }
    return computation;
  }
}
