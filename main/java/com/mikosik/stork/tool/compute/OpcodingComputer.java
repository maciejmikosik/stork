package com.mikosik.stork.tool.compute;

import com.mikosik.stork.data.model.Opcode;
import com.mikosik.stork.data.model.comp.Computation;

public class OpcodingComputer implements Computer {
  private final Computer computer;

  private OpcodingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer opcoding(Computer computer) {
    return new OpcodingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Opcode
        ? ((Opcode) computation.expression).compute(computation.stack)
        : computer.compute(computation);
  }
}
