package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestRunnerEngine {
  public static Suite testRunnerEngine() {
    return suite("test runner engine")
        .add(storkTest("application argument can be integer")
            .givenMocks("f")
            .when("f(2)")
            .thenReturned("f(2)"))
        .add(storkTest("application argument can be lambda")
            .givenMocks("mock")
            .given("f(g)(x) { g(x) }")
            .when("f((x){x})(mock)")
            .thenReturned("mock"))
        .add(storkTest("applied argument is not accidently bound to lambda parameter")
            .givenMocks("f", "x", "y", "z")
            .given("tuple(x)(y)(f) { f(x)(y) }")
            .when("tuple(y)(z)(f)")
            .thenReturned("f(y)(z)"));
  }
}
