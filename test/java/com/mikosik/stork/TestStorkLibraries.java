package com.mikosik.stork;

import static com.mikosik.stork.TestBooleanLibrary.testBooleanLibrary;
import static com.mikosik.stork.TestFunctionLibrary.testFunctionLibrary;
import static com.mikosik.stork.TestOptionalLibrary.testOptionalLibrary;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStorkLibraries {
  public static Test testStorkLibraries() {
    return suite("stork libraries")
        .add(testFunctionLibrary())
        .add(testBooleanLibrary())
        .add(testOptionalLibrary());
  }
}
