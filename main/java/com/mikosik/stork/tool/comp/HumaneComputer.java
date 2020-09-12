package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;

import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;

public class HumaneComputer implements Computer {
  private final Computer computer;

  private HumaneComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer humane(Computer computer) {
    return new HumaneComputer(computer);
  }

  public Computation compute(Computation computation) {
    Computation computed = computer.compute(computation);
    return isHumane(computed)
        ? computed
        : computation;
  }

  private static boolean isHumane(Computation computation) {
    while (computation.expression instanceof Lambda) {
      Lambda lambda = (Lambda) computation.expression;
      if (computation.stack instanceof Argument) {
        Argument argument = (Argument) computation.stack;
        computation = computation(lambda.body, argument.stack);
      } else {
        return false;
      }
    }
    return true;
  }
}
