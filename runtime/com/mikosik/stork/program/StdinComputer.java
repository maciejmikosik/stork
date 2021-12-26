package com.mikosik.stork.program;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.tool.common.Constants.NONE;
import static com.mikosik.stork.tool.common.Constants.SOME;
import static com.mikosik.stork.tool.compute.CachingComputer.caching;

import com.mikosik.stork.common.io.MaybeByte;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.tool.compute.Computer;

public class StdinComputer implements Computer {
  private final Computer computer;

  private StdinComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer stdin(Computer computer) {
    return caching(new StdinComputer(computer));
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Stdin
        ? computation(compute((Stdin) computation.expression), computation.stack)
        : computer.compute(computation);
  }

  private Expression compute(Stdin stdin) {
    MaybeByte maybeByte = stdin.input.read();
    return maybeByte.hasByte()
        ? application(
            SOME,
            integer(maybeByte.getByte()),
            Stdin.stdin(stdin.input))
        : NONE;
  }
}
