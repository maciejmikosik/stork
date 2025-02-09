package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.cases.TestBoolean.testBoolean;
import static com.mikosik.stork.test.cases.TestFunction.testFunction;
import static com.mikosik.stork.test.cases.TestInteger.testInteger;
import static com.mikosik.stork.test.cases.TestMaybe.testMaybe;
import static com.mikosik.stork.test.cases.TestStream.testStream;
import static com.mikosik.stork.test.cases.TestStreamCount.testStreamCount;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testBoolean())
        .add(testFunction())
        .add(testInteger())
        .add(testStream())
        .add(testStreamCount())
        .add(testMaybe());
  }
}
