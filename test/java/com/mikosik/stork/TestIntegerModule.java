package com.mikosik.stork;

import static com.mikosik.stork.testing.ModuleTest.moduleTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestIntegerModule {
  public static Test testIntegerModule() {
    return suite("integer.stork")
        .add(suite("parsing")
            .add(moduleTest("0", "0"))
            .add(moduleTest("1", "1"))
            .add(moduleTest("-1", "-1"))
            .add(moduleTest("100000000000000000000", "100000000000000000000"))
            .add(moduleTest("-100000000000000000000", "-100000000000000000000")))
        .add(suite("parsing noncanonical")
            .add(moduleTest("-0", "0"))
            .add(moduleTest("+0", "0"))
            .add(moduleTest("00", "0"))
            .add(moduleTest("-00", "0"))
            .add(moduleTest("+00", "0"))
            .add(moduleTest("01", "1"))
            .add(moduleTest("-01", "-1"))
            .add(moduleTest("+01", "1")))
        .add(suite("add")
            .add(moduleTest("add(2)(3)", "5"))
            .add(moduleTest("add(-1)(1)", "0"))
            .add(moduleTest("add(-10)(-5)", "-15")))
        .add(suite("negate")
            .add(moduleTest("negate(0)", "0"))
            .add(moduleTest("negate(5)", "-5"))
            .add(moduleTest("negate(-3)", "3")))
        .add(suite("equal")
            .add(moduleTest("equal(0)(0)", "true"))
            .add(moduleTest("equal(1)(1)", "true"))
            .add(moduleTest("equal(-1)(-1)", "true"))
            .add(moduleTest("equal(0)(1)", "false"))
            .add(moduleTest("equal(1)(0)", "false"))
            .add(moduleTest("equal(1)(-1)", "false"))
            .add(moduleTest("equal(-1)(1)", "false")))
        .add(suite("moreThan")
            .add(moduleTest("moreThan(0)(1)", "true"))
            .add(moduleTest("moreThan(1)(0)", "false"))
            .add(moduleTest("moreThan(0)(0)", "false"))
            .add(moduleTest("moreThan(-1)(1)", "true"))
            .add(moduleTest("moreThan(1)(-1)", "false")))
        .add(suite("handles uncomputed arguments")
            .add(moduleTest("add(add(1)(2))(add(3)(4))", "10"))
            .add(moduleTest("negate(negate(5))", "5"))
            .add(moduleTest("equal(negate(1))(negate(2))", "false"))
            .add(moduleTest("moreThan(negate(2))(negate(3))", "false")));
  }
}
