package com.mikosik.stork;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.lib.Modules.module;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;

import java.io.InputStream;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;
import com.mikosik.stork.tool.link.Linker;

public class RunDemo {
  public static void main(String[] args) {
    Chain<Module> modules = chainOf(
        module("opcode.stork"),
        module("function.stork"),
        module("stream.stork"),
        module("integer.stork"),
        module("boolean.stork"),
        module("program.stork"),
        module("demo.stork"));

    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(modules);

    Program program = program(variable("main"), linkedModule);
    InputStream input = program.run();
    pump(input, System.out);
    System.out.flush();
  }
}
