package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkModuleTest.testEqual;
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
            .add(testEqual("negate(-3)", "3")))
        .add(suite("equal")
            .add(testEqual("equal(0)(0)", "true"))
            .add(testEqual("equal(1)(1)", "true"))
            .add(testEqual("equal(-1)(-1)", "true"))
            .add(testEqual("equal(0)(1)", "false"))
            .add(testEqual("equal(1)(0)", "false"))
            .add(testEqual("equal(1)(-1)", "false"))
            .add(testEqual("equal(-1)(1)", "false")))
        .add(suite("moreThan")
            .add(testEqual("moreThan(0)(1)", "true"))
            .add(testEqual("moreThan(1)(0)", "false"))
            .add(testEqual("moreThan(0)(0)", "false"))
            .add(testEqual("moreThan(-1)(1)", "true"))
            .add(testEqual("moreThan(1)(-1)", "false")));
  }
}
