package com.mikosik.stork.testing;

import static com.mikosik.stork.testing.StorkTest.storkTest;
import static java.lang.String.format;

import org.quackery.Test;

public class StorkModuleTest {
  public static Test testEqual(String expression, String expected) {
    return storkTest()
        .name(format("%s = %s", expression, expected))
        .humane()
        .givenImported("boolean.stork")
        .givenImported("integer.stork")
        .givenImported("stream.stork")
        .givenImported("optional.stork")
        .givenImported("function.stork")
        .givenMocks(name -> name.length() == 1)
        .when(expression)
        .thenReturned(expected);
  }
}
