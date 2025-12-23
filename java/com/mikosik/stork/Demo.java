package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;

public class Demo {
  public static void main(String[] args) {
    var project = project();
    var library = compile(compilation()
        .source(project.demoDirectory.directory("greeting"))
        .library(core(DEVELOPMENT)));

    program(identifier("main"), library)
        .run(input(System.in), output(System.out));
  }
}
