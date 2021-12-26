package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.test.ProgramTest.testProgramsIn;
import static com.mikosik.stork.test.TestDecompiler.testDecompiler;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testProgramsIn(path("runtime_test")))
        .add(testProgramsIn(path("core_star_test")))
        .add(testDecompiler()));
  }
}
