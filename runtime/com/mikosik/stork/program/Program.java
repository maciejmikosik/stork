package com.mikosik.stork.program;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Check.check;
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
import static com.mikosik.stork.model.change.Changes.inModule;
import static com.mikosik.stork.program.ProgramModule.WRITE_STREAM;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;
import static com.mikosik.stork.tool.common.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.tool.link.CheckCollisions.checkCollisions;
import static com.mikosik.stork.tool.link.CheckUndefined.checkUndefined;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.MathModule.mathModule;
import static com.mikosik.stork.tool.link.Unlambda.unlambda;
import static com.mikosik.stork.tool.link.Unquote.unquote;

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

  public void run(Input stdinInput, Output stdout) {
    Module linkedModule = link(chain(
        mathModule(),
        combinatoryModule(),
        programModule(stdout),
        module));

    checkCollisions(linkedModule);
    checkUndefined(linkedModule);

    linkedModule = inModule(unlambda)
        .andThen(inModule(unquote))
        .apply(linkedModule);

    Computer expressing = chained(
        modulingComputer(linkedModule),
        instructionComputer(),
        applicationComputer(),
        stdinComputer(),
        returningComputer());
    Computer computer = looping(interruptible(caching(expressing)));

    Computation computation = computation(
        application(
            WRITE_STREAM,
            application(main, stdin(stdinInput))));

    Computation computed = computer.compute(computation);
    stdout.close();
    check(computed.stack.isEmpty());
  }
}
