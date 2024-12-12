package com.mikosik.stork.compute;

import static com.mikosik.stork.common.UncheckedInterruptedException.uncheckedInterruptedException;

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
      throw uncheckedInterruptedException();
    }
    return computer.compute(computation);
  }
}
