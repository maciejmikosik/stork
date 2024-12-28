package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Logic.untilIdempotent;

public class LoopingComputer implements Computer {
  private final Computer computer;

  private LoopingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer looping(Computer computer) {
    return new LoopingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return untilIdempotent(computer::compute)
        .apply(computation);
  }
}
