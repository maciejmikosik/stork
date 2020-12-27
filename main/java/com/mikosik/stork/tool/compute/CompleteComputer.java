package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.common.Computations.abort;

import com.mikosik.stork.model.Expression;

public class CompleteComputer {
  private final Computer computer;

  private CompleteComputer(Computer computer) {
    this.computer = computer;
  }

  public static CompleteComputer complete(Computer computer) {
    return new CompleteComputer(computer);
  }

  public Expression compute(Expression expression) {
    return abort(computer.compute(computation(expression)));
  }
}
