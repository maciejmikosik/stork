package com.mikosik.stork.program;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.ChainedComputer.chained;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.compute.LoopingComputer.looping;
import static com.mikosik.stork.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.compute.OperatorComputer.operatorComputer;
import static com.mikosik.stork.compute.ReturningComputer.returningComputer;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.writeStreamTo;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.compute.Stack.Empty;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

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
        operatorComputer(),
        applicationComputer(),
        returningComputer()))));
  }

  public void run(Input input, Output output) {
    var computation = computation(application(
        writeStreamTo(output),
        application(main, stdin(input))));
    var computed = computer.compute(computation);
    output.close();
    check(computed.stack instanceof Empty);
  }
}
