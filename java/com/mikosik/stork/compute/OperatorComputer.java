package com.mikosik.stork.compute;

import com.mikosik.stork.model.exp.Operator;

public class OperatorComputer extends TypedComputer<Operator> {
  private OperatorComputer() {
    super(Operator.class);
  }

  public static TypedComputer<Operator> operatorComputer() {
    return new OperatorComputer();
  }

  public Computation compute(Operator operator, Stack stack) {
    return operator.compute(stack);
  }
}
