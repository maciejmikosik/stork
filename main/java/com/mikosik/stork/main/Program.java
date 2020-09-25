package com.mikosik.stork.main;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;

import java.io.InputStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.link.Linker;

public class Program {
  private final String main;
  private final Chain<Module> modules;

  private Program(String main, Chain<Module> modules) {
    this.main = main;
    this.modules = modules;
  }

  public static Program program(String main, Chain<Module> modules) {
    return new Program(main, modules);
  }

  public InputStream run() {
    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(modules);
    Computer computer = computer()
        .module(linkedModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(WritingComputer::writing);

    return new InputStream() {
      boolean closed;
      Computation computation = computation(
          application(
              variable("writeStream"),
              variable(main)));

      public int read() {
        if (closed) {
          return -1;
        }
        computation = computer.compute(computation);
        Argument argument = (Argument) computation.stack;
        int oneByte = asJavaBigInteger(argument.expression).intValueExact();
        check(-1 <= oneByte && oneByte <= 255);
        closed = (oneByte == -1);
        return oneByte;
      }

      public void close() {
        closed = true;
      }
    };
  }
}
