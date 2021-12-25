package com.mikosik.stork.demo;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.common.InputOutput.path;
import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = link(chainOf(
        moduleFromDirectory(path("demo/com/mikosik/stork/demo")),
        moduleFromDirectory(path("core_star"))));

    Program program = program(variable("main"), module);
    program.run(input(System.in), output(System.out));
  }
}
