package com.mikosik.stork.testing;

import static com.mikosik.stork.testing.StorkTest.storkTest;

import org.quackery.Test;

public class StorkModuleTest {
  public static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("boolean.stork")
        .givenImported("integer.stork")
        .givenImported("stream.stork")
        .givenImported("optional.stork")
        .givenImported("function.stork")
        .givenMocks("x", "y", "z", "f", "g", "h", "i")
        .when(expression)
        .thenReturned(expected);
  }
}
