package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static org.logbuddy.decorator.NoDecorator.noDecorator;

import java.nio.file.Paths;

public class Demo {
  private final static boolean isLogging = false;

  public static void main(String[] args) {
    var project = project();
    var library = compile(compilation()
        .source(project.demoDirectory.directory("greeting"))
        .library(core(DEVELOPMENT)));

    var decorator = isLogging
        ? configuredDecorator(Paths.get("/tmp/stork.log"))
        : noDecorator();

    decorator.decorate(program(identifier("main"), library))
        .run(input(System.in), output(System.out));
  }
}
