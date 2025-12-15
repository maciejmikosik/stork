package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;

import com.mikosik.stork.compute.Stack.Function;
import com.mikosik.stork.model.Integer;

public class IntegerComputer extends TypedComputer<Integer> {
  private IntegerComputer() {
    super(Integer.class);
  }

  public static Computer integerComputer() {
    return new IntegerComputer();
  }

  public Computation compute(Integer integer, Stack stack) {
    return switch (stack) {
      case Function function -> computation(
          application(function.expression, integer),
          function.previous);
      // TODO report that you cannot apply integer as function
      default -> throw cannotCompute();
    };
  }
}
