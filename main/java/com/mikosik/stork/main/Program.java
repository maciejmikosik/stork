package com.mikosik.stork.main;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.main.StreamingComputer.writeStream;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
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

  public void run() {
    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(modules);
    Computer computer = computer()
        .module(linkedModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(StreamingComputer::streaming);

    for (Computation computation = computation(
        application(writeStream, variable(main)));;) {

      do {
        computation = computer.compute(computation);
      } while (!(computation.expression instanceof Streamed));
      Streamed streamed = (Streamed) computation.expression;
      if (streamed.oneByte == -1) {
        break;
      }
      System.out.write(streamed.oneByte);
      System.out.flush();
    }
  }
}
