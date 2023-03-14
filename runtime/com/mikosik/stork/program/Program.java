package com.mikosik.stork.program;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Sequence.sequence;
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
import static com.mikosik.stork.program.ProgramModule.WRITE_STREAM;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;
import static com.mikosik.stork.program.Stdout.stdout;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class Program {
  private final Expression main;
  private final Module module;

  private Program(Expression main, Module module) {
    this.main = main;
    this.module = module;
  }

  public static Program program(Identifier main, Module module) {
    return new Program(main, module);
  }

  public void run(Input input, Output output) {
    Computer expressing = chained(sequence(
        modulingComputer(module),
        instructionComputer(),
        applicationComputer(),
        stdinComputer(),
        returningComputer()));
    Computer computer = looping(interruptible(caching(expressing)));

    Computation computation = computation(
        application(
            WRITE_STREAM,
            stdout(output),
            application(main, stdin(input))));

    Computation computed = computer.compute(computation);
    output.close();
    check(computed.stack.isEmpty());
  }
}
