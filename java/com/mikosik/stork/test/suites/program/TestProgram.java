package com.mikosik.stork.test.suites.program;

import static com.mikosik.stork.test.suites.program.TestImportCore.testImportCore;
import static com.mikosik.stork.test.suites.program.TestMainFunction.testMainFunction;
import static com.mikosik.stork.test.suites.program.TestMathOperators.testMathOperators;
import static com.mikosik.stork.test.suites.program.TestStdio.testStdio;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestProgram {
  public static Test testProgram() {
    return suite("program")
        .add(testMainFunction())
        .add(testStdio())
        .add(testImportCore())
        .add(testMathOperators());
  }
}
