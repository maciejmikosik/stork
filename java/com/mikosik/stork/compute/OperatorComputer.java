package com.mikosik.stork.compute;

import com.mikosik.stork.model.Operator;

public class OperatorComputer extends TypedComputer<Operator> {
  private OperatorComputer() {
    super(Operator.class);
  }

  public static Computer operatorComputer() {
    return new OperatorComputer();
  }

  public Computation compute(Operator operator, Stack stack) {
    return operator.compute(stack);
  }
}
