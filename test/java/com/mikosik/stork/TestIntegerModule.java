package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestIntegerModule {
  public static Test testIntegerModule() {
    return suite("integer.stork")
        .add(suite("parsing")
            .add(testEqual("0", "0"))
            .add(testEqual("1", "1"))
            .add(testEqual("-1", "-1"))
            .add(testEqual("100000000000000000000", "100000000000000000000"))
            .add(testEqual("-100000000000000000000", "-100000000000000000000")))
        .add(suite("parsing noncanonical")
            .add(testEqual("-0", "0"))
            .add(testEqual("+0", "0"))
            .add(testEqual("00", "0"))
            .add(testEqual("-00", "0"))
            .add(testEqual("+00", "0"))
            .add(testEqual("01", "1"))
            .add(testEqual("-01", "-1"))
            .add(testEqual("+01", "1")))
        .add(suite("add")
            .add(testEqual("add(2)(3)", "5"))
            .add(testEqual("add(-1)(1)", "0"))
            .add(testEqual("add(-10)(-5)", "-15")))
        .add(suite("negate")
            .add(testEqual("negate(0)", "0"))
            .add(testEqual("negate(5)", "-5"))
            .add(testEqual("negate(-3)", "3")));
  }

  private static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("integer.stork")
        .when(expression)
        .thenReturned(expected);
  }
}
