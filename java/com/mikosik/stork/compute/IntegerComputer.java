package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

import com.mikosik.stork.compute.Stack.Function;
import com.mikosik.stork.model.Integer;

public class IntegerComputer implements Computer {
  private IntegerComputer() {}

  public static Computer integerComputer() {
    return new IntegerComputer();
  }

  public Computation compute(Computation computation) {
    return switch (computation.expression) {
      case Integer integer -> switch (computation.stack) {
        case Function function -> computation(
            application(function.expression, computation.expression),
            function.previous);
        default -> computation;
      };
      default -> computation;
    };
  }
}
