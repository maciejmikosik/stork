package com.mikosik.stork.test.suites.documentation;

import static com.mikosik.stork.test.suites.documentation.TestFunction.testFunction;
import static com.mikosik.stork.test.suites.documentation.TestStdio.testStdio;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestDocumentation {
  public static Test testDocumentation() {
    return suite("documentation")
        .add(testStdio())
        .add(testFunction());
  }
}
