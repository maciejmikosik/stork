package com.mikosik.stork.program;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.compile.link.Combinator.I;
import static com.mikosik.stork.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.ChainedComputer.chained;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.compute.LoopingComputer.looping;
import static com.mikosik.stork.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.compute.ReturningComputer.returningComputer;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;

import java.util.Optional;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.compute.Stack.Empty;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Operator;

public class Program {
  private final Expression main;
  private final Computer computer;

  private Program(Expression main, Computer computer) {
    this.main = main;
    this.computer = computer;
  }

  public static Program program(Identifier main, Module module) {
    return new Program(main, buildComputer(module));
  }

  private static Computer buildComputer(Module module) {
    return looping(interruptible(caching(chained(
        modulingComputer(module),
        instructionComputer(),
        applicationComputer(),
        stdinComputer(),
        returningComputer()))));
  }

  public void run(Input input, Output output) {
    var reduce = identifier("lang.stream.reduceEager");
    var eager = identifier("lang.op.EAGER");

    var computation = computation(
        application(
            reduce,
            CLOSE_STREAM,
            application(eager, writeByteTo(output)),
            application(main, stdin(input))));

    var computed = computer.compute(computation);
    output.close();
    check(computed.stack instanceof Empty);
  }

  public static Expression writeByteTo(Output output) {
    return new Operator() {
      public Optional<Computation> compute(Stack stack) {
        if (stack instanceof Argument argumentA
            && argumentA.expression instanceof Integer integer
            && argumentA.previous instanceof Argument argumentB) {
          int oneByte = integer.value.intValueExact();
          check(0 <= oneByte && oneByte <= 255);
          output.write((byte) oneByte);
          return Optional.of(computation(I, argumentB));
        } else {
          return Optional.empty();
        }
      }

      public String toString() {
        return "WRITE";
      }
    };
  }

  public static final Expression CLOSE_STREAM = new Operator() {
    public Optional<Computation> compute(Stack stack) {
      return Optional.empty();
    }

    public String toString() {
      return "CLOSE";
    }
  };
}
