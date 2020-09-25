package com.mikosik.stork.main;

import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;

public class WritingComputer implements Computer {
  private final Computer nextComputer;

  private WritingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer writing(Computer nextComputer) {
    return new WritingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    do {
      computation = nextComputer.compute(computation);
    } while (!hasWrittenByte(computation));
    return computation;
  }

  private static boolean hasWrittenByte(Computation computation) {
    return computation.expression instanceof Variable
        && ((Variable) computation.expression).name.equals("writeByte");
  }
}
