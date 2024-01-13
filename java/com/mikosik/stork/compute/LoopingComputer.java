package com.mikosik.stork.compute;

public class LoopingComputer implements Computer {
  private final Computer computer;

  private LoopingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer looping(Computer computer) {
    return new LoopingComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation previous = null;
    do {
      previous = computation;
      computation = computer.compute(computation);
    } while (computation != previous);
    return computation;
  }
}
