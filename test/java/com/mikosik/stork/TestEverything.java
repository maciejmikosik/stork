package com.mikosik.stork;

import static com.mikosik.stork.TestBooleanModule.testBooleanModule;
import static com.mikosik.stork.TestComputer.testComputer;
import static com.mikosik.stork.TestFunctionModule.testFunctionModule;
import static com.mikosik.stork.TestIntegerModule.testIntegerModule;
import static com.mikosik.stork.TestOptionalModule.testOptionalModule;
import static com.mikosik.stork.TestStringModule.testStringModule;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testComputer())
        .add(suite("stork modules")
            .add(testFunctionModule())
            .add(testBooleanModule())
            .add(testIntegerModule())
            .add(testOptionalModule())
            .add(testStringModule())));
  }
}
