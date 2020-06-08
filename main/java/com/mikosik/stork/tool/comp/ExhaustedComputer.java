package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.Computation;
import com.mikosik.stork.data.model.Expression;

public class ExhaustedComputer implements Computer {
  private final Computer computer;

  private ExhaustedComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer exhausted(Computer computer) {
    return new ExhaustedComputer(computer);
  }

  public Expression compute(Expression expression) {
    Expression result = computer.compute(expression);
    while (result instanceof Computation) {
      result = computer.compute(result);
    }
    return result;
  }
}
