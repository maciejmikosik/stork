package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.Printer.print;
import static com.mikosik.stork.tool.common.Computations.abort;

import com.mikosik.stork.data.model.comp.Computation;

public class LoggingComputer implements Computer {
  private final Computer computer;

  private LoggingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer logging(Computer computer) {
    return new LoggingComputer(computer);
  }

  public Computation compute(Computation computation) {
    System.out.println(print(abort(mark(computation))));
    return computer.compute(computation);
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }
}
