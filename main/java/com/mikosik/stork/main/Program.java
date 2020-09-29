package com.mikosik.stork.main;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;

import java.io.InputStream;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;

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

  public InputStream run() {
    Computer computer = computer()
        .module(module)
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
              main));

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
