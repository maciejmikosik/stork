package com.mikosik.stork.compute;

import static com.mikosik.stork.program.Stdout.CLOSE;

import com.mikosik.stork.compute.Stack.Empty;

public class LoopingComputer implements Computer {
  private final Computer computer;

  private LoopingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer looping(Computer computer) {
    return new LoopingComputer(computer);
  }

  public Computation compute(Computation computation) {
    do {
      computation = computer.compute(computation);
    } while (!isDone(computation));
    return computation;
  }

  private boolean isDone(Computation computation) {
    return computation.expression == CLOSE
        && computation.stack instanceof Empty;
  }
}
