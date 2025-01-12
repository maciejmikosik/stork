package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.Compiler.nativeModule;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static org.logbuddy.decorator.NoDecorator.noDecorator;

import java.nio.file.Paths;

public class Demo {
  private final static boolean isLogging = false;

  public static void main(String[] args) {
    var project = project();
    var module = compile(compilation()
        .source(project.demoDirectory)
        .source(project.coreLibraryDirectory)
        .library(nativeModule()));

    var decorator = isLogging
        ? configuredDecorator(Paths.get("/tmp/stork.log"))
        : noDecorator();

    decorator.decorate(program(identifier("main"), module))
        .run(input(System.in), output(System.out));
  }
}
