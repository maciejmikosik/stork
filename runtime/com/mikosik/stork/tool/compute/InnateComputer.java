package com.mikosik.stork.tool.compute;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Innate;

public class InnateComputer implements Computer {
  private InnateComputer() {}

  public static Computer innateComputer() {
    return new InnateComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Innate
        ? ((Innate) computation.expression).compute(computation.stack)
        : computation;
  }
}
