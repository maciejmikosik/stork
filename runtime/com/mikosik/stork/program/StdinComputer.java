package com.mikosik.stork.program;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.tool.common.Constants.NONE;
import static com.mikosik.stork.tool.common.Constants.SOME;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.common.io.MaybeByte;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;
import com.mikosik.stork.tool.compute.Computer;

public class StdinComputer implements Computer {
  private final Map<Stdin, Expression> cache = new WeakHashMap<>();

  private StdinComputer() {}

  public static Computer stdinComputer() {
    return new StdinComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Stdin
        ? compute((Stdin) computation.expression, computation.stack)
        : computation;
  }

  private Computation compute(Stdin stdin, Stack stack) {
    return computation(
        cache.computeIfAbsent(stdin, this::compute),
        stack);
  }

  private Expression compute(Stdin stdin) {
    MaybeByte maybeByte = head(stdin);
    return maybeByte.hasByte()
        ? application(
            SOME,
            integer(maybeByte.getByte()),
            tail(stdin))
        : NONE;
  }

  private static MaybeByte head(Stdin stdin) {
    return stdin.input.read();
  }

  private static Expression tail(Stdin stdin) {
    return Stdin.stdin(stdin.input, stdin.index + 1);
  }
}
