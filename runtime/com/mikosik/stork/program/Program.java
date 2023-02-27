package com.mikosik.stork.program;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;
import static com.mikosik.stork.program.Stdout.writeStream;
import static com.mikosik.stork.tool.common.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.tool.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.tool.compute.CachingComputer.caching;
import static com.mikosik.stork.tool.compute.ChainedComputer.chained;
import static com.mikosik.stork.tool.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.tool.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.tool.compute.LoopingComputer.looping;
import static com.mikosik.stork.tool.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.tool.compute.ReturningComputer.returningComputer;
import static com.mikosik.stork.tool.link.Changes.inExpression;
import static com.mikosik.stork.tool.link.Changes.inModule;
import static com.mikosik.stork.tool.link.CheckCollisions.checkCollisions;
import static com.mikosik.stork.tool.link.CheckUndefined.checkUndefined;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.MathModule.mathModule;
import static com.mikosik.stork.tool.link.Unlambda.unlambda;
import static com.mikosik.stork.tool.link.Unquote.unquote;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compute.Computer;

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
    Module linkedModule = link(chainOf(
        mathModule(),
        combinatoryModule(),
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
            inExpression(unlambda).apply(writeStream(stdout)),
            application(main, stdin(stdinInput))));

    Computation computed = computer.compute(computation);
    stdout.close();
    check(computed.stack.isEmpty());
  }
}
