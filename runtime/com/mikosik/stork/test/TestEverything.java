package com.mikosik.stork.test;

import static com.mikosik.stork.test.ProgramTest.testProgramsIn;
import static com.mikosik.stork.test.TestDecompiler.testDecompiler;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import java.nio.file.Paths;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testProgramsIn(Paths.get("runtime_test")))
        .add(testProgramsIn(Paths.get("core_star_test")))
        .add(testDecompiler()));
  }
}
