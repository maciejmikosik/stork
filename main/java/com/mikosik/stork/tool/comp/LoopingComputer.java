package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.comp.Computation;

public class LoopingComputer implements Computer {
  private final Computer computer;

  private LoopingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer looping(Computer computer) {
    return new LoopingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation computed = computer.compute(computation);
    while (computed != computation) {
      computation = computed;
      computed = computer.compute(computation);
    }
    return computed;
  }
}
