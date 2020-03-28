package com.mikosik.lang.run;

import com.mikosik.lang.model.Expression;

public class Runner {
  private Runner() {}

  public static Runner runner() {
    return new Runner();
  }

  public Expression run(Expression expression) {
    return expression;
  }
}
