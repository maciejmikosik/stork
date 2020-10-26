package com.mikosik.stork.tool.comp;

import java.io.OutputStream;
import java.io.PrintStream;

import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.Decompiler;

public class LoggingComputer implements Computer {
  private final PrintStream output;
  private final Decompiler decompiler;
  private final Computer computer;

  private LoggingComputer(PrintStream output, Decompiler decompiler, Computer computer) {
    this.output = output;
    this.decompiler = decompiler;
    this.computer = computer;
  }

  public static Computer logging(
      OutputStream output,
      Decompiler decompiler,
      Computer computer) {
    return new LoggingComputer(
        new PrintStream(output),
        decompiler,
        computer);
  }

  public Computation compute(Computation computation) {
    output.println(decompiler.decompile(computation));
    return computer.compute(computation);
  }
}
