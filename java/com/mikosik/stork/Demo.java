package com.mikosik.stork;

import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.problem.VerifyModule.verify;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static org.logbuddy.decorator.NoDecorator.noDecorator;

import java.nio.file.Paths;

import org.logbuddy.Decorator;

import com.mikosik.stork.model.Module;

public class Demo {
  private final static boolean isLogging = false;

  public static void main(String[] args) {
    Module module = build(verify(join(sequence(
        moduleFromDirectory(Paths.get("demo/com/mikosik/stork/demo")),
        moduleFromDirectory(Paths.get("core_library")),
        programModule(),
        combinatoryModule(),
        mathModule()))));

    Decorator decorator = isLogging
        ? configuredDecorator(Paths.get("/tmp/stork.log"))
        : noDecorator();

    decorator.decorate(program(identifier("main"), module))
        .run(input(System.in), output(System.out));
  }
}
