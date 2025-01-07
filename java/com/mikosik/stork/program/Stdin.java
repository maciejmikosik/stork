package com.mikosik.stork.program;

import static com.mikosik.stork.compile.link.Bridge.NONE;
import static com.mikosik.stork.compile.link.Bridge.some;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;

import java.util.Optional;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;

public class Stdin implements Operator {
  private final Input input;
  private final int index;
  private Expression computed = null;

  private Stdin(Input input, int index) {
    this.input = input;
    this.index = index;
  }

  public static Expression stdin(Input input) {
    return new Stdin(input, 0);
  }

  public static Expression stdin(Input input, int index) {
    return new Stdin(input, index);
  }

  public synchronized Optional<Computation> compute(Stack stack) {
    if (computed == null) {
      var maybeByte = input.read();
      computed = maybeByte.hasByte()
          ? some(
              integer(maybeByte.getByte()),
              stdin(input, index + 1))
          : NONE;
    }
    return Optional.of(computation(computed, stack));
  }

  public String toString() {
    return "STDIN(%s)".formatted(index);
  }
}
