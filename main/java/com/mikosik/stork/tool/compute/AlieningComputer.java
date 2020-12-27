package com.mikosik.stork.tool.compute;

import com.mikosik.stork.model.Alien;
import com.mikosik.stork.model.Computation;

public class AlieningComputer implements Computer {
  private final Computer computer;

  private AlieningComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer aliening(Computer computer) {
    return new AlieningComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Alien
        ? ((Alien) computation.expression).compute(computation.stack)
        : computer.compute(computation);
  }
}
