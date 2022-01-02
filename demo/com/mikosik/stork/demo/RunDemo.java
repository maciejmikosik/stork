package com.mikosik.stork.demo;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = link(chainOf(
        moduleFromDirectory(node("demo/com/mikosik/stork/demo")),
        moduleFromDirectory(node("core_star"))));

    Program program = program(identifier("main"), module);
    program.run(input(System.in), output(System.out));
  }
}
