package com.mikosik.stork.test.suites.program;

import static com.mikosik.stork.compute.err.FunctionMissing.functionMissing;
import static com.mikosik.stork.model.exp.Reference.reference;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestMainFunction {
  public static Test testMainFunction() {
    return suite("root file must have main function")
        .add(rootFileMustExist())
        .add(rootFileCannotBeEmpty())
        .add(rootFileMustContainMain());
  }

  private static Test rootFileMustExist() {
    return programTest("must exist")
        .expect(functionMissing(reference(variable("main"))));
  }

  private static Test rootFileCannotBeEmpty() {
    return programTest("cannot be empty")
        .source("")
        .expect(functionMissing(reference(variable("main"))));
  }

  private static Test rootFileMustContainMain() {
    return programTest("must contain main")
        .source("notMain(stdin) { '' }")
        .expect(functionMissing(reference(variable("main"))));
  }
}
