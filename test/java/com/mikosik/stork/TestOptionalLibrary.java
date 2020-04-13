package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestOptionalLibrary {
  public static Test testOptionalLibrary() {
    return suite("optional.stork")
        .add(suite("present/absent")
            .add(testEqual("present(x)(f)(g)", "f(x)"))
            .add(testEqual("absent(f)(g)", "g")))
        .add(suite("else")
            .add(testEqual("else(y)(present(x))", "x"))
            .add(testEqual("else(y)(absent)", "y")));
  }

  private static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("function.stork")
        .givenImported("optional.stork")
        .givenMocks("x", "y", "f", "g")
        .when(expression)
        .thenReturned(expected);
  }
}
