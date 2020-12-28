package com.mikosik.stork.front.program;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Variable.variable;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;
import com.mikosik.stork.model.Variable;
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
    int oneByte = stdin.input.read();
    return oneByte == -1
        ? none
        : application(
            some,
            integer(oneByte),
            Stdin.stdin(stdin.input));
  }
}
