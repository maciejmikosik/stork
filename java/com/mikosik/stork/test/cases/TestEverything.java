package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.cases.TestCoreLibrary.testCoreLibrary;
import static com.mikosik.stork.test.cases.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.cases.TestProgram.testProgram;
import static java.nio.file.Files.createTempFile;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.emptyList;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertTrue;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

import com.mikosik.stork.compile.Compiler;
import com.mikosik.stork.model.Instruction;

public class TestEverything {
  public static Test testEverything() {
    return timeout(ofSeconds(1), suite("test everything")
        .add(testProgram())
        .add(testDecompiler())
        .add(testCompiler())
        .add(testComputers())
        .add(testLogbuddyDecorator())
        .add(testCoreLibrary()));
  }

  private static Test testCompiler() {
    canCompile("identifier", "function");
    return suite("compiler can compile standalone")
        .add(canCompile("identifier", "function"))
        .add(canCompile("application", "function(argument)"))
        .add(canCompile("lambda", "(x){ x }"))
        .add(canCompile("integer", "123"));
  }

  private static Test canCompile(String testName, String source) {
    return newCase(testName, () -> {
      new Compiler().compileExpression(input(source));
    });
  }

  private static Test testComputers() {
    return suite("computers can handle empty stack")
        .add(newCase("eager", () -> {
          var computer = instructionComputer();
          var computation = computation(eager(x -> identifier("y")));
          var computed = computer.compute(computation);
          assertTrue(computation == computed);
        }))
        .add(newCase("instruction", () -> {
          var computer = instructionComputer();
          var computation = computation((Instruction) argument -> argument);
          var computed = computer.compute(computation);
          assertTrue(computation == computed);
        }));
  }

  private static Test testLogbuddyDecorator() {
    return newCase("logbuddy decorator can decorate program", () -> {
      var module = module(emptyList());
      var logFile = createTempFile("stork_test_", "");
      configuredDecorator(logFile)
          .decorate(program(identifier("main"), module));
    });
  }
}
