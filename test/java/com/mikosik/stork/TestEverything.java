package com.mikosik.stork;

import static com.mikosik.stork.TestComputer.testComputer;
import static com.mikosik.stork.TestProgram.testProgram;
import static com.mikosik.stork.testing.ModuleTest.testModule;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testComputer())
        .add(testProgram())
        .add(suite("modules")
            .add(testModule("function.stork", "testFunction"))
            .add(testModule("boolean.stork", "testBoolean"))
            .add(testModule("optional.stork", "testOptional"))
            .add(testModule("integer.stork", "testInteger"))
            .add(testModule("stream.stork", "testStream"))));
  }
}
