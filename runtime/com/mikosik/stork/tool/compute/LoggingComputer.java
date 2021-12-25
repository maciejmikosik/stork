package com.mikosik.stork.tool.compute;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.PrintStream;

import com.mikosik.stork.common.Output;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.tool.decompile.Decompiler;

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
      Output output,
      Decompiler decompiler,
      Computer computer) {
    return new LoggingComputer(
        output.asPrintStream(UTF_8),
        decompiler,
        computer);
  }

  public Computation compute(Computation computation) {
    output.println(decompiler.decompile(computation));
    return computer.compute(computation);
  }
}
