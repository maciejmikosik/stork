package com.mikosik.stork.tool.compute;

import com.mikosik.stork.model.Computation;

public class LoopingComputer implements Computer {
  private final Computer computer;

  private LoopingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer looping(Computer computer) {
    return new LoopingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation computed = computation;
    do {
      computation = computed;
      computed = computer.compute(computation);
    } while (computed != computation);
    return computed;
  }
}
