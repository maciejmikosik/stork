package com.mikosik.stork;

import static com.mikosik.stork.testing.ModuleTest.moduleTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestFunctionModule {
  public static Test testFunctionModule() {
    return suite("function.stork")
        .add(suite("self")
            .add(moduleTest("self(x)", "x")))
        .add(suite("compose")
            .add(moduleTest("compose(f)(g)(x)", "f(g(x))"))
            .add(moduleTest("compose(f)(compose(g)(h))(x)", "f(g(h(x)))"))
            .add(moduleTest("compose(compose(f)(g))(h)(x)", "f(g(h(x)))"))
            .add(moduleTest("compose(compose(f)(g))(compose(h)(i))(x)", "f(g(h(i(x))))")))
        .add(suite("flip")
            .add(moduleTest("flip(f)(x)(y)", "f(y)(x)"))
            .add(moduleTest("flip(f)(x)(y)(z)", "f(y)(x)(z)")));
  }
}
