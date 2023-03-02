package com.mikosik.stork.demo;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Link.link;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = link(chain(
        moduleFromDirectory(node("demo/com/mikosik/stork/demo")),
        moduleFromDirectory(node("core_star"))));

    Program program = program(identifier("main"), module);
    program.run(input(System.in), output(System.out));
  }
}
