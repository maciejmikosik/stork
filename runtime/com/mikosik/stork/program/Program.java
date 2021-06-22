package com.mikosik.stork.program;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.program.InnateMath.innateMath;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.writeStream;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.CheckCollisions.checkCollisions;
import static com.mikosik.stork.tool.link.CheckUndefined.checkUndefined;
import static com.mikosik.stork.tool.link.Redefine.redefine;
import static com.mikosik.stork.tool.link.Unlambda.unlambda;
import static com.mikosik.stork.tool.link.Unquote.unquote;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.common.Output;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.compute.Computer;

public class Program {
  private final Expression main;
  private final Module module;

  private Program(Expression main, Module module) {
    this.main = main;
    this.module = module;
  }

  public static Program program(Variable main, Module module) {
    return new Program(main, module);
  }

  public void run(Input stdinInput, Output stdout) {
    Module linkedModule = redefine(innateMath(), module);

    checkCollisions(linkedModule);
    checkUndefined(linkedModule);

    linkedModule = unquote(unlambda(linkedModule));

    Computer computer = computer()
        .stacking()
        .moduling(linkedModule)
        .innate()
        .combinatorial()
        .wire(StdinComputer::stdin)
        .caching()
        .interruptible()
        .looping();

    Computation computation = computation(
        application(
            unlambda(writeStream(stdout)),
            application(main, stdin(stdinInput))));

    computer.compute(computation);
  }
}
