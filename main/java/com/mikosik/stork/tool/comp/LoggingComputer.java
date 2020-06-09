package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.Printer.print;

import com.mikosik.stork.data.model.Expression;

public class LoggingComputer implements Computer {
  private final Computer computer;

  private LoggingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer logging(Computer computer) {
    return new LoggingComputer(computer);
  }

  public Expression compute(Expression expression) {
    System.out.println(print(expression));
    return computer.compute(expression);
  }
}
