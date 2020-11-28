package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.core.CoreModule.coreModule;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.compile.Compiler.compiler;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import java.io.InputStream;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;
import com.mikosik.stork.tool.compile.Compiler;
import com.mikosik.stork.tool.link.Linker;

public class RunDemo {
  public static void main(String[] args) {
    Compiler compiler = compiler();
    Linker linker = linker()
        .building()
        .unique()
        .coherent();
    Module module = linker.link(chainOf(
        compiler.compile(resource("demo.stork")),
        coreModule()));
    Program program = program(variable("main"), module);
    pump(program.run(System.in), System.out);
    System.out.flush();
  }

  private static InputStream resource(String name) {
    return RunDemo.class.getResourceAsStream(name);
  }
}
