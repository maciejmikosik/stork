package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestFunctionLibrary {
  public static Test testFunctionLibrary() {
    return suite("function.stork")
        .add(suite("self")
            .add(testEqual("self(x)", "x")))
        .add(suite("compose")
            .add(testEqual("compose(f)(g)(x)", "f(g(x))"))
            .add(testEqual("compose(f)(compose(g)(h))(x)", "f(g(h(x)))"))
            .add(testEqual("compose(compose(f)(g))(h)(x)", "f(g(h(x)))"))
            .add(testEqual("compose(compose(f)(g))(compose(h)(i))(x)", "f(g(h(i(x))))")))
        .add(suite("flip")
            .add(testEqual("flip(f)(x)(y)", "f(y)(x)"))
            .add(testEqual("flip(f)(x)(y)(z)", "f(y)(x)(z)")));
  }

  private static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("core.stork")
        .givenImported("function.stork")
        .givenMocks("x", "y", "z", "f", "g", "h", "i")
        .when(expression)
        .thenReturned(expected);
  }
}
