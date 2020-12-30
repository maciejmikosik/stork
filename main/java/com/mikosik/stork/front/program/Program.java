package com.mikosik.stork.front.program;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.front.core.CoreModules.coreModules;
import static com.mikosik.stork.front.core.JavaModule.javaModule;
import static com.mikosik.stork.front.program.Stdin.stdin;
import static com.mikosik.stork.front.program.StdoutModule.closeStream;
import static com.mikosik.stork.front.program.StdoutModule.stdoutModule;
import static com.mikosik.stork.front.program.StdoutModule.writeByte;
import static com.mikosik.stork.front.program.StdoutModule.writeStream;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.Builder.builder;
import static com.mikosik.stork.tool.link.Linkers.compose;
import static com.mikosik.stork.tool.link.Linkers.linker;
import static com.mikosik.stork.tool.link.NameCollisionDetector.nameCollisionDetector;
import static com.mikosik.stork.tool.link.QuoteStreamer.quoteStreamer;
import static com.mikosik.stork.tool.link.UndefinedVariablesDetector.undefinedVariablesDetector;

import java.io.InputStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compute.Computer;
import com.mikosik.stork.tool.link.Linker;

public class Program {
  private final Expression main;
  private final Chain<Module> modules;

  private Program(Expression main, Chain<Module> modules) {
    this.main = main;
    this.modules = modules;
  }

  public static Program program(Expression main, Chain<Module> modules) {
    return new Program(main, modules);
  }

  public Input run(Input stdinInput) {
    Linker linker = linker(
        builder(),
        compose(
            quoteStreamer(),
            nameCollisionDetector(),
            undefinedVariablesDetector()));

    Module module = linker.link(modules
        .addAll(coreModules())
        .add(javaModule())
        .add(stdoutModule()));

    Computer computer = computer()
        .stacking()
        .moduling(module)
        .aliening()
        .substituting()
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
