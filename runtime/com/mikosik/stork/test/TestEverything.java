package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.test.ProgramTest.testProgramsIn;
import static com.mikosik.stork.test.Runners.timeout;
import static com.mikosik.stork.test.TestDecompiler.testDecompiler;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.tool.compile.Compiler;

public class TestEverything {
  public static Test testEverything() {
    return timeout(1, suite("test everything")
        .add(testProgramsIn(node("runtime_test")))
        .add(testProgramsIn(node("core_star_test")))
        .add(testDecompiler())
        .add(testCompiler()));
  }

  private static Test testCompiler() {
    Compiler compiler = new Compiler();
    return suite("compiler can compile standalone")
        .add(newCase("variable",
            () -> compiler.compileExpression(input("function"))))
        .add(newCase("application",
            () -> compiler.compileExpression(input("function(argument)"))))
        .add(newCase("lambda",
            () -> compiler.compileExpression(input("(x){ x }"))))
        .add(newCase("integer",
            () -> compiler.compileExpression(input("123"))));
  }
}
