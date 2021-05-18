package com.mikosik.stork.front.program;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.front.program.InnateMathModule.innateMathModule;
import static com.mikosik.stork.front.program.Stdin.stdin;
import static com.mikosik.stork.front.program.StdoutModule.closeStream;
import static com.mikosik.stork.front.program.StdoutModule.stdoutModule;
import static com.mikosik.stork.front.program.StdoutModule.writeByte;
import static com.mikosik.stork.front.program.StdoutModule.writeStream;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.CheckCollisions.checkCollisions;
import static com.mikosik.stork.tool.link.CheckUndefined.checkUndefined;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Unlambda.unlambda;
import static com.mikosik.stork.tool.link.Unquote.unquote;

import java.io.InputStream;

import com.mikosik.stork.common.Input;
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

  public Input run(Input stdinInput) {
    Module linkedModule = link(chainOf(module)
        .add(innateMathModule())
        .add(stdoutModule()));

    checkCollisions(linkedModule);
    checkUndefined(linkedModule);

    linkedModule = unquote(unlambda(linkedModule));

    Computer computer = computer()
        .stacking()
        .moduling(linkedModule)
        .innate()
        .wire(StdinComputer::stdin)
        .caching()
        .interruptible()
        .progressing()
        .wire(StdoutComputer::stdout);

    return input(new InputStream() {
      boolean closed = false;
      Computation computation = computation(
          application(
              writeStream,
              application(main, stdin(stdinInput))));

      public int read() {
        if (closed) {
          return -1;
        }
        computation = computer.compute(computation);
        if (computation.expression == writeByte) {
          int oneByte = computation.stack
              .argumentIntegerJava()
              .intValueExact();
          check(0 <= oneByte && oneByte <= 255);
          return oneByte;
        } else if (computation.expression == closeStream) {
          closed = true;
          return -1;
        } else {
          throw new RuntimeException();
        }
      }

      public void close() {
        closed = true;
      }
    });
  }
}
