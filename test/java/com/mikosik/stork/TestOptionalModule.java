package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkModuleTest.testEqual;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestOptionalModule {
  public static Test testOptionalModule() {
    return suite("optional.stork")
        .add(suite("present/absent")
            .add(testEqual("present(x)(f)(g)", "f(x)"))
            .add(testEqual("absent(f)(g)", "g")))
        .add(suite("else")
            .add(testEqual("else(y)(present(x))", "x"))
            .add(testEqual("else(y)(absent)", "y")));
  }
}
