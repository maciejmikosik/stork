package com.mikosik.stork.tool.run;

import static com.mikosik.stork.tool.Printer.print;

import com.mikosik.stork.data.model.Expression;

public class LoggingRunner implements Runner {
  private final Runner runner;

  private LoggingRunner(Runner runner) {
    this.runner = runner;
  }

  public static Runner logging(Runner runner) {
    return new LoggingRunner(runner);
  }

  public Expression run(Expression expression) {
    System.out.println(print(expression));
    return runner.run(expression);
  }
}
