package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;

public class ExhaustedComputer implements Computer {
  private final Computer computer;

  private ExhaustedComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer exhausted(Computer computer) {
    return new ExhaustedComputer(computer);
  }

  public Computation compute(Computation computation) {
    while (!(computation.stack instanceof Empty)
        || computation.expression instanceof Variable
        || computation.expression instanceof Application) {
      computation = computer.compute(computation);
    }
    return computation;
  }
}
