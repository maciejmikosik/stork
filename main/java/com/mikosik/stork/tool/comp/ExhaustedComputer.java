package com.mikosik.stork.tool.comp;

import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;
import com.mikosik.stork.tool.common.Computations;

public class ExhaustedComputer implements Computer {
  private final Computer computer;

  private ExhaustedComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer exhausted(Computer computer) {
    return new ExhaustedComputer(computer);
  }

  public Computation compute(Computation computation) {
    return isComputable(computation)
        ? computer.compute(computation)
        : computation;
  }

  private static boolean isComputable(Computation computation) {
    return !(computation.stack instanceof Empty)
        || Computations.isComputable(computation.expression);
  }
}
