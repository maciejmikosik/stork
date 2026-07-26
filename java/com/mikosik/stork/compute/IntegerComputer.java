package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.exp.Application.application;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;
import static com.mikosik.stork.problem.compute.ComputerException.exception;

import com.mikosik.stork.compute.Stack.Function;
import com.mikosik.stork.model.exp.Integer;

public class IntegerComputer extends TypedComputer<Integer> {
  private IntegerComputer() {
    super(Integer.class);
  }

  public static TypedComputer<Integer> integerComputer() {
    return new IntegerComputer();
  }

  public Computation compute(Integer integer, Stack stack) {
    return switch (stack) {
      case Function function -> computation(
          application(function.expression, integer),
          function.previous);
      // TODO report that you cannot apply integer as function
      default -> throw exception(cannotCompute());
    };
  }
}
