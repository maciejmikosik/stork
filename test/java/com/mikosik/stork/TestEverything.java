package com.mikosik.stork;

import static com.mikosik.stork.testing.ModuleTest.testModule;
import static com.mikosik.stork.testing.ProgramTest.testProgramsIn;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import java.nio.file.Paths;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testProgramsIn(Paths.get("test/story")))
        .add(suite("modules")
            .add(testModule("literals.test.stork"))
            .add(testModule("function.test.stork"))
            .add(testModule("boolean.test.stork"))
            .add(testModule("optional.test.stork"))
            .add(testModule("integer.test.stork"))));
  }
}
