package com.mikosik.stork;

import static com.mikosik.stork.testing.ModuleTest.moduleTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestBooleanModule {
  public static Test testBooleanModule() {
    return suite("boolean.stork")
        .add(suite("true/false")
            .add(moduleTest("true(x)(y)", "x"))
            .add(moduleTest("false(x)(y)", "y")))
        .add(suite("not")
            .add(moduleTest("not(true)", "false"))
            .add(moduleTest("not(false)", "true")))
        .add(suite("and")
            .add(moduleTest("and(true)(true)", "true"))
            .add(moduleTest("and(false)(true)", "false"))
            .add(moduleTest("and(true)(false)", "false"))
            .add(moduleTest("and(false)(false)", "false")))
        .add(suite("or")
            .add(moduleTest("or(true)(true)", "true"))
            .add(moduleTest("or(false)(true)", "true"))
            .add(moduleTest("or(true)(false)", "true"))
            .add(moduleTest("or(false)(false)", "false")));
  }
}
