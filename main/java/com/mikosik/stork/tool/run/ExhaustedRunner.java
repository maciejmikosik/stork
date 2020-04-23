package com.mikosik.stork.tool.run;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Running;

public class ExhaustedRunner implements Runner {
  private final Runner runner;

  private ExhaustedRunner(Runner runner) {
    this.runner = runner;
  }

  public static Runner exhausted(Runner runner) {
    return new ExhaustedRunner(runner);
  }

  public Expression run(Expression expression) {
    Expression result = runner.run(expression);
    while (result instanceof Running) {
      result = runner.run(result);
    }
    return result;
  }
}
