package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.test.ProgramTest.testProgramsIn;
import static com.mikosik.stork.test.TestDecompiler.testDecompiler;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testProgramsIn(node("runtime_test")))
        .add(testProgramsIn(node("core_star_test")))
        .add(testDecompiler()));
  }
}