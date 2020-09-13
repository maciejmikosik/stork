package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.comp.Computation;

public class LoopingComputer implements Computer {
  private final Computer nextComputer;

  private LoopingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer looping(Computer nextComputer) {
    return new LoopingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    Computation computed = nextComputer.compute(computation);
    while (computed != computation) {
      computation = computed;
      computed = nextComputer.compute(computation);
    }
    return computed;
  }
}
