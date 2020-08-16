package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Computations.print;

import com.mikosik.stork.data.model.comp.Computation;

public class DefaultComputer implements Computer {
  private DefaultComputer() {}

  public static Computer computer() {
    return new DefaultComputer();
  }

  public Computation compute(Computation computation) {
    throw new RuntimeException(print(computation));
  }
}
