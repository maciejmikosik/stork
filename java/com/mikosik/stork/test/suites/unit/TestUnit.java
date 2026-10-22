package com.mikosik.stork.test.suites.unit;

import static com.mikosik.stork.test.suites.unit.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.suites.unit.TestDescriber.testDescriber;
import static com.mikosik.stork.test.suites.unit.TestLogbuddyDecorator.testLogbuddyDecorator;
import static com.mikosik.stork.test.suites.unit.TestOutcome.testOutcome;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestUnit {
  public static Test testUnit() {
    return suite("unit")
        .add(suite("debug tools")
            .add(testDecompiler())
            .add(testLogbuddyDecorator()))
        .add(testDescriber())
        .add(testOutcome());
  }
}
