package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.common.Input.resource;
import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.front.program.Program.program;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;
import static com.mikosik.stork.tool.link.Build.build;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;

import java.nio.file.Paths;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.front.program.Program;
import com.mikosik.stork.model.Module;

public class RunDemo {
  public static void main(String[] args) {
    Module module = link(chainOf(
        build(defaultCompiler().compile(file("demo.stork").buffered())),
        moduleFromDirectory(Paths.get("main/stork/com/mikosik"))));

    Program program = program(variable("main"), module);
    program.run(input(System.in))
        .pumpToAndFlush(output(System.out));
  }

  private static Input file(String name) {
    return resource(RunDemo.class, name);
  }
}
