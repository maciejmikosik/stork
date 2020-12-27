package com.mikosik.stork.main;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.main.Stdin.stdin;
import static com.mikosik.stork.main.StdoutModule.closeStream;
import static com.mikosik.stork.main.StdoutModule.stdoutModule;
import static com.mikosik.stork.main.StdoutModule.writeByte;
import static com.mikosik.stork.main.StdoutModule.writeStream;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import java.io.InputStream;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.compute.Computer;
import com.mikosik.stork.tool.link.Linker;

public class Program {
  private final Expression main;
  private final Module module;

  private Program(Expression main, Module module) {
    this.main = main;
    this.module = module;
  }

  public static Program program(Expression main, Module module) {
    return new Program(main, module);
  }

  public Input run(Input stdinInput) {
    Linker linker = linker()
        .unique()
        .coherent();

    Module linkedModule = linker.link(chainOf(
        stdoutModule(),
        module));

    Computer computer = computer()
        .moduling(linkedModule)
        .aliening()
        .substituting()
        .stacking()
        .wire(StdinComputer::stdin)
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
