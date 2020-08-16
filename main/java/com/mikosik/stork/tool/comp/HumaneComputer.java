package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Computations.isComputable;
import static com.mikosik.stork.tool.common.Computations.isHumane;

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
    while (isComputable(computation)) {
      Computation computed = computer.compute(computation);
      if (isHumane(computed)) {
        computation = computed;
      } else {
        break;
      }
    }
    return computation;
  }
}
