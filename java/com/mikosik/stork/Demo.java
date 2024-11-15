package com.mikosik.stork;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compiler.compileCoreLibrary;
import static com.mikosik.stork.compile.Compiler.compileDirectory;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.compile.link.VerifyModule.verify;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.debug.InjectNames.injectNames;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static org.logbuddy.decorator.NoDecorator.noDecorator;

import java.nio.file.Paths;

import org.logbuddy.Decorator;

import com.mikosik.stork.model.Module;

public class Demo {
  private final static boolean isLogging = false;

  public static void main(String[] args) {
    Module module = verify(join(
        compileDirectory(path("demo")),
        maybeInjectNames(compileCoreLibrary(Paths.get("core_library")))));

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
