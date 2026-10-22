package com.mikosik.stork.test.suites.core;

import static com.mikosik.stork.test.suites.core.TestBoolean.testBoolean;
import static com.mikosik.stork.test.suites.core.TestFunction.testFunction;
import static com.mikosik.stork.test.suites.core.TestInteger.testInteger;
import static com.mikosik.stork.test.suites.core.TestList.testList;
import static com.mikosik.stork.test.suites.core.TestListCount.testListCount;
import static com.mikosik.stork.test.suites.core.TestMaybe.testMaybe;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testBoolean())
        .add(testFunction())
        .add(testInteger())
        .add(testList())
        .add(testListCount())
        .add(testMaybe());
  }
}
