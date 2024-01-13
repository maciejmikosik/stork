package com.mikosik.stork.program;

import static com.mikosik.stork.compile.Bridge.NONE;
import static com.mikosik.stork.compile.Bridge.some;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.common.io.MaybeByte;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Expression;

public class StdinComputer implements Computer {
  private final Map<Stdin, Expression> cache = new WeakHashMap<>();

  private StdinComputer() {}

  public static Computer stdinComputer() {
    return new StdinComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Stdin stdin
        ? computation(
            computeIfAbsent(stdin),
            computation.stack)
        : computation;
  }

  private Expression computeIfAbsent(Stdin stdin) {
    return cache.computeIfAbsent(stdin, this::compute);
  }

  private Expression compute(Stdin stdin) {
    MaybeByte maybeByte = head(stdin);
    return maybeByte.hasByte()
        ? some(
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
