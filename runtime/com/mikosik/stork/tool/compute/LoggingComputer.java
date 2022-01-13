package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Variable.variable;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.PrintStream;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;
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
    output.println(decompiler.decompile(abort(mark(computation))));
    return computer.compute(computation);
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }

  private static Expression abort(Computation computation) {
    Expression expression = computation.expression;
    Stack stack = computation.stack;
    while (true) {
      if (stack.hasArgument()) {
        expression = application(expression, stack.argument());
      } else if (stack.hasFunction()) {
        expression = application(stack.function(), expression);
      } else {
        break;
      }
      stack = stack.pop();
    }
    return expression;
  }
}
