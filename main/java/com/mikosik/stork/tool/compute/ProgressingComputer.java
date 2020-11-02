package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.common.Check.check;

import com.mikosik.stork.data.model.comp.Computation;

public class ProgressingComputer implements Computer {
  private final Computer computer;

  private ProgressingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer progressing(Computer computer) {
    return new ProgressingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation computed = computer.compute(computation);
    check(computation != computed);
    return computed;
  }
}
