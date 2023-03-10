package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.test.ProgramTest.testProgramsIn;
import static com.mikosik.stork.test.Runners.timeout;
import static com.mikosik.stork.test.cases.TestCoreLibrary.testCoreLibrary;
import static com.mikosik.stork.test.cases.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.cases.TestProgram.testProgram;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertTrue;

import java.nio.file.Paths;

import org.quackery.Test;

import com.mikosik.stork.compile.Compiler;
import com.mikosik.stork.model.Instruction;

public class TestEverything {
  public static Test testEverything() {
    return timeout(1, suite("test everything")
        .add(testProgramsIn(Paths.get("runtime_test")))
        .add(testProgram())
        .add(testDecompiler())
        .add(testCompiler())
        .add(testComputers())
        .add(testCoreLibrary()));
  }

  private static Test testCompiler() {
    Compiler compiler = new Compiler();
    return suite("compiler can compile standalone")
        .add(newCase("identifier",
            () -> compiler.compileExpression(input("function"))))
        .add(newCase("application",
            () -> compiler.compileExpression(input("function(argument)"))))
        .add(newCase("lambda",
            () -> compiler.compileExpression(input("(x){ x }"))))
        .add(newCase("integer",
            () -> compiler.compileExpression(input("123"))));
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
}
