package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Computations.isComputable;

import com.mikosik.stork.data.model.comp.Computation;

public class ExhaustedComputer implements Computer {
  private final Computer computer;

  private ExhaustedComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer exhausted(Computer computer) {
    return new ExhaustedComputer(computer);
  }

  public Computation compute(Computation computation) {
    return isComputable(computation)
        ? computer.compute(computation)
        : computation;
  }
}
