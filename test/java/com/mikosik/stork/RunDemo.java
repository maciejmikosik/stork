package com.mikosik.stork;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.common.InputOutput.readResource;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.Default.compileModule;
import static com.mikosik.stork.tool.link.Linker.coreModule;
import static com.mikosik.stork.tool.link.Linker.link;

import java.io.InputStream;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = link(chainOf(
        compileModule(readResource(RunDemo.class, "demo.stork")),
        coreModule()));
    Program program = program(variable("main"), module);
    InputStream input = program.run();
    pump(input, System.out);
    System.out.flush();
  }
}
