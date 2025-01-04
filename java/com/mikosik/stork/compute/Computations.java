package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

import java.util.Map;
import java.util.WeakHashMap;

import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.compute.Stack.Empty;
import com.mikosik.stork.compute.Stack.Frame;
import com.mikosik.stork.compute.Stack.Function;
import com.mikosik.stork.model.Expression;

public class Computations {
  public static boolean areSame(Computation first, Computation second) {
    return first.expression == second.expression
        && first.stack == second.stack;
  }

  public static Expression abort(Computation computation) {
    while (computation.stack instanceof Frame frame) {
      computation = switch (frame) {
        case Argument argument -> computation(
            application(computation.expression, argument.expression),
            argument.previous);
        case Function function -> computation(
            application(function.expression, computation.expression),
            function.previous);
        default -> throw runtimeException("unknown frame %s", frame);
      };
    }
    return computation.expression;
  }

  public static int depthOf(Stack stack) {
    return stackDepth.computeIfAbsent(stack, Computations::computeDepthOf);
  }

  private static int computeDepthOf(Stack stack) {
    return switch (stack) {
      case Empty empty -> 0;
      case Frame frame -> depthOf(frame.previous) + 1;
    };
  }

  private static final Map<Stack, Integer> stackDepth = new WeakHashMap<>();
}
