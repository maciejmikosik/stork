package com.mikosik.stork.compute;

import com.mikosik.stork.model.Operator;

public class OperatorComputer implements Computer {
  private OperatorComputer() {}

  public static Computer operatorComputer() {
    return new OperatorComputer();
  }

  public Computation compute(Computation computation) {
    return switch (computation.expression) {
      case Operator operator -> operator
          .compute(computation.stack)
          .orElse(computation);
      default -> computation;
    };
  }
}
