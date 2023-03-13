package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static com.mikosik.stork.test.cases.TestBoolean.testBoolean;
import static com.mikosik.stork.test.cases.TestFunction.testFunction;
import static com.mikosik.stork.test.cases.TestInteger.testInteger;
import static com.mikosik.stork.test.cases.TestStream.testStream;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testBoolean())
        .add(testFunction())
        .add(testInteger())
        .add(testStream())
        .add(testOptional());
  }

  private static Test testOptional() {
    return snippetTest("optional")
        .importing("lang.optional.present")
        .importing("lang.optional.absent")
        .importing("lang.optional.else")
        .test("present(1) ((x){x})(2)", "1")
        .test("absent     ((x){x})(2)", "2")
        .test("else(2)(present(1))", "1")
        .test("else(2)(absent)    ", "2");
  }
}
