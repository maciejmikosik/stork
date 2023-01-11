package com.mikosik.stork.test;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testProofOfConcept());
  }

  public static Test testProofOfConcept() {
    return snippetTest("boolean")
        .importing("stork.integer.add")
        .importing("stork.integer.negate")
        .importing("stork.boolean.true")
        .importing("stork.boolean.false")
        .importing("stork.boolean.and")
        .test("and(true)(true)", "true")
        .test("negate(2)", "-2");
  }
}
