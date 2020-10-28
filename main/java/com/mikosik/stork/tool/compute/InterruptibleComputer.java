package com.mikosik.stork.tool.compute;

import com.mikosik.stork.common.UncheckedInterruptedException;
import com.mikosik.stork.data.model.comp.Computation;

public class InterruptibleComputer implements Computer {
  private final Computer computer;

  private InterruptibleComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer interruptible(Computer computer) {
    return new InterruptibleComputer(computer);
  }

  public Computation compute(Computation computation) {
    if (Thread.interrupted()) {
      throw new UncheckedInterruptedException();
    }
    return computer.compute(computation);
  }
}
