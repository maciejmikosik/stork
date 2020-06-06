package com.mikosik.stork;

import static com.mikosik.stork.TestBooleanModule.testBooleanModule;
import static com.mikosik.stork.TestFunctionModule.testFunctionModule;
import static com.mikosik.stork.TestIntegerModule.testIntegerModule;
import static com.mikosik.stork.TestOptionalModule.testOptionalModule;
import static com.mikosik.stork.TestRunner.testRunner;
import static com.mikosik.stork.TestStringModule.testStringModule;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return suite("test everything")
        .add(testRunner())
        .add(suite("stork modules")
            .add(testFunctionModule())
            .add(testBooleanModule())
            .add(testIntegerModule())
            .add(testOptionalModule())
            .add(testStringModule()));
  }
}
