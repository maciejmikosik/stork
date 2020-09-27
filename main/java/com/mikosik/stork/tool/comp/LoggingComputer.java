package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.Decompiler.decompiler;

import java.io.OutputStream;
import java.io.PrintStream;

import com.mikosik.stork.data.model.comp.Computation;

public class LoggingComputer implements Computer {
  private final PrintStream output;
  private final Computer computer;

  private LoggingComputer(PrintStream output, Computer computer) {
    this.output = output;
    this.computer = computer;
  }

  public static Computer logging(OutputStream output, Computer computer) {
    return new LoggingComputer(new PrintStream(output), computer);
  }

  public Computation compute(Computation computation) {
    output.println(decompiler().decompile(computation));
    return computer.compute(computation);
  }
}
