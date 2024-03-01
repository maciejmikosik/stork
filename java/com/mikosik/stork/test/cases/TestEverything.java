package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.io.InputOutput.delete;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.debug.Debug.configuredDecorator;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.cases.TestBuild.testBuild;
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

import com.mikosik.stork.model.Instruction;

public class TestEverything {
  public static Test testEverything() {
    return timeout(ofSeconds(1), suite("test everything")
        .add(testProgram())
        .add(testBuild())
        .add(testDecompiler())
        .add(testComputers())
        .add(testLogbuddyDecorator())
        .add(testCoreLibrary()));
  }

  private static Test testComputers() {
    return suite("computers")
        .add(suite("can handle empty stack")
            .add(newCase("eager computer", () -> {
              var computer = instructionComputer();
              var computation = computation(eager(x -> identifier("y")));
              var computed = computer.compute(computation);
              assertTrue(computation == computed);
            }))
            .add(newCase("instruction computer", () -> {
              var computer = instructionComputer();
              var computation = computation((Instruction) argument -> argument);
              var computed = computer.compute(computation);
              assertTrue(computation == computed);
            })))
        .add(suite("caching computer")
            .add(newCase("does not duplicate computation", () -> {
              var computer = caching(computation -> computation);
              var computation = computation(variable("variable"));
              var computed = computer.compute(computation);
              assertTrue(computation == computed);
              computed = computer.compute(computed);
              assertTrue(computation == computed);
              computed = computer.compute(computed);
              assertTrue(computation == computed);
            })));
  }

  private static Test testLogbuddyDecorator() {
    return newCase("logbuddy decorator can decorate program", () -> {
      var module = module(emptyList());
      var logFile = createTempFile("stork_test_", "");
      configuredDecorator(logFile)
          .decorate(program(identifier("main"), module));
      delete(logFile);
    });
  }
}
