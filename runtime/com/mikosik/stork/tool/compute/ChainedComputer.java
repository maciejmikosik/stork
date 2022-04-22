package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.common.Chain.chainOf;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Computation;

public class ChainedComputer implements Computer {
  private final Chain<Computer> computers;

  private ChainedComputer(Chain<Computer> computers) {
    this.computers = computers;
  }

  public static Computer chained(Chain<Computer> computers) {
    return new ChainedComputer(computers);
  }

  public static Computer chained(Computer... computers) {
    return new ChainedComputer(chainOf(computers));
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
