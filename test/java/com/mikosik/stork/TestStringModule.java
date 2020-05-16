package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStringModule {
  public static Test testStringModule() {
    return suite("string.stork")
        .add(suite("parsing")
            .add(testEqual("\"\"", "none"))
            .add(testEqual("\"a\"", "some(97)(none)"))
            .add(testEqual("\"012\"", "some(48)(some(49)(some(50)(none)))")));
  }

  private static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("stream.stork")
        .when(expression)
        .thenReturned(expected);
  }
}
