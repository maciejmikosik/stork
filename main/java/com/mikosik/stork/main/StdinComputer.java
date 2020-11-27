package com.mikosik.stork.main;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;
import com.mikosik.stork.tool.compute.Computer;

public class StdinComputer implements Computer {
  private final Variable some = variable("stork.stream.some");
  private final Variable none = variable("stork.stream.none");
  private final Map<Stdin, Expression> cache = new WeakHashMap<>();

  private final Computer computer;

  private StdinComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer stdin(Computer computer) {
    return new StdinComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Stdin
        ? compute((Stdin) computation.expression, computation.stack)
        : computer.compute(computation);
  }

  private Computation compute(Stdin stdin, Stack stack) {
    return computation(
        cache.computeIfAbsent(stdin, this::compute),
        stack);
  }

  private Expression compute(Stdin stdin) {
    try {
      int oneByte = stdin.input.read();
      return oneByte == -1
          ? none
          : application(
              some,
              integer(BigInteger.valueOf(oneByte)),
              Stdin.stdin(stdin.input));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
