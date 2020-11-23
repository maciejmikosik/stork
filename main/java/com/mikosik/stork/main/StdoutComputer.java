package com.mikosik.stork.main;

import static com.mikosik.stork.main.StdoutModule.closeStream;
import static com.mikosik.stork.main.StdoutModule.writeByte;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.compute.Computer;

public class StdoutComputer implements Computer {
  private final Computer computer;

  private StdoutComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer stdout(Computer computer) {
    return new StdoutComputer(computer);
  }

  public Computation compute(Computation computation) {
    do {
      computation = computer.compute(computation);
    } while (!isStdoutEvent(computation.expression));
    return computation;
  }

  private static boolean isStdoutEvent(Expression expression) {
    return expression == writeByte
        || expression == closeStream;
  }
}
