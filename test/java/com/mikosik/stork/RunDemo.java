package com.mikosik.stork;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.lib.Modules.module;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.link.Linker.link;
import static com.mikosik.stork.tool.link.Linker.programModule;

import java.io.InputStream;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;

public class RunDemo {
  public static void main(String[] args) {
    Program program = program(variable("main"), demoModule());
    InputStream input = program.run();
    pump(input, System.out);
    System.out.flush();
  }

  public static Module demoModule() {
    return link(chainOf(
        module("demo.stork"),
        programModule()));
  }
}
