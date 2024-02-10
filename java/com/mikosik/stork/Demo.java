package com.mikosik.stork;

import static com.mikosik.stork.build.Stars.build;
import static com.mikosik.stork.build.Stars.moduleFromDirectory;
import static com.mikosik.stork.build.link.Bind.join;
import static com.mikosik.stork.build.link.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.build.link.MathModule.mathModule;
import static com.mikosik.stork.build.link.problem.VerifyModule.verify;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.debug.InjectNames.injectNames;
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
        moduleFromDirectory(Paths.get("demo")),
        moduleFromDirectory(Paths.get("core_library")),
        maybeInjectNames(programModule()),
        maybeInjectNames(combinatoryModule()),
        maybeInjectNames(mathModule())))));

    Decorator decorator = isLogging
        ? configuredDecorator(Paths.get("/tmp/stork.log"))
        : noDecorator();

    decorator.decorate(program(identifier("main"), module))
        .run(input(System.in), output(System.out));
  }

  private static Module maybeInjectNames(Module module) {
    return isLogging
        ? injectNames(module)
        : module;
  }
}
