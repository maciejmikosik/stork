package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.common.Input.resource;
import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.core.CoreModule.coreModule;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import com.mikosik.stork.main.Program;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compile.Compiler;
import com.mikosik.stork.tool.link.Linker;

public class RunDemo {
  public static void main(String[] args) {
    Compiler<Module> compiler = defaultCompiler();
    Linker linker = linker()
        .building()
        .unique()
        .coherent();
    Module module = linker.link(chainOf(
        compiler.compile(resource(RunDemo.class, "demo.stork").buffered()),
        coreModule()));
    Program program = program(variable("main"), module);
    program.run(input(System.in))
        .pumpToAndFlush(output(System.out));
  }
}
