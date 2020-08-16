package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Substitute.substitute;

import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;

public class SubstitutingComputer implements Computer {
  private final Computer nextComputer;

  private SubstitutingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer substituting(Computer nextComputer) {
    return new SubstitutingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Lambda
        && computation.stack instanceof Argument
            ? compute(
                (Lambda) computation.expression,
                (Argument) computation.stack)
            : nextComputer.compute(computation);
  }

  private Computation compute(Lambda lambda, Argument argument) {
    return computation(
        substitute(lambda, argument.expression),
        argument.stack);
  }
}
