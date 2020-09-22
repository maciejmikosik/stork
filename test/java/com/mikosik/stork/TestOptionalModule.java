package com.mikosik.stork;

import static com.mikosik.stork.testing.ModuleTest.moduleTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestOptionalModule {
  public static Test testOptionalModule() {
    return suite("optional.stork")
        .add(suite("present/absent")
            .add(moduleTest("present(x)(f)(g)", "f(x)"))
            .add(moduleTest("absent(f)(g)", "g")))
        .add(suite("else")
            .add(moduleTest("else(y)(present(x))", "x"))
            .add(moduleTest("else(y)(absent)", "y")));
  }
}
