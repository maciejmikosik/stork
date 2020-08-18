package com.mikosik.stork.tool.comp;

import com.mikosik.stork.common.UncheckedInterruptedException;
import com.mikosik.stork.data.model.comp.Computation;

public class InterruptibleComputer implements Computer {
  private final Computer nextComputer;

  private InterruptibleComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer interruptible(Computer nextComputer) {
    return new InterruptibleComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    if (Thread.interrupted()) {
      throw new UncheckedInterruptedException();
    }
    return nextComputer.compute(computation);
  }
}
