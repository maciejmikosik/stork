package com.mikosik.stork.main;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.InputOutput.readResource;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import java.io.InputStream;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
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

  public InputStream run() {
    Linker linker = linker()
        .unique()
        .coherent();
    Module programModule = linker.link(chainOf(
        modelModule(parse(readResource(Program.class, "program.stork"))),
        module));

    Computer computer = computer()
        .moduling(programModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(Program::writingBytes);

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

  private static Computer writingBytes(Computer computer) {
    return new Computer() {
      public Computation compute(Computation computation) {
        do {
          computation = computer.compute(computation);
        } while (!hasWrittenByte(computation));
        return computation;
      }

      private boolean hasWrittenByte(Computation computation) {
        return computation.expression instanceof Variable
            && ((Variable) computation.expression).name.equals("writeByte");
      }
    };
  }
}
