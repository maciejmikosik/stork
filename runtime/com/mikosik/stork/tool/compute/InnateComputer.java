package com.mikosik.stork.tool.compute;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Innate;

public class InnateComputer implements Computer {
  private final Computer computer;

  private InnateComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer innate(Computer computer) {
    return new InnateComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Innate
        ? ((Innate) computation.expression).compute(computation.stack)
        : computer.compute(computation);
  }
}
