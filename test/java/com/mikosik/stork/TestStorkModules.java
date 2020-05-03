package com.mikosik.stork;

import static com.mikosik.stork.TestBooleanModule.testBooleanModule;
import static com.mikosik.stork.TestFunctionModule.testFunctionModule;
import static com.mikosik.stork.TestOptionalModule.testOptionalModule;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStorkModules {
  public static Test testStorkModules() {
    return suite("stork modules")
        .add(testFunctionModule())
        .add(testBooleanModule())
        .add(testOptionalModule());
  }
}
