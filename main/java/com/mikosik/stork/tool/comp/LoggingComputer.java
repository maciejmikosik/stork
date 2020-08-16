package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.Printer.print;

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
    System.out.println(print(computation));
    return computer.compute(computation);
  }
}
