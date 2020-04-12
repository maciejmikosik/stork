package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestRunnerEngine {
  public static Suite testRunnerEngine() {
    return suite("test runner engine")
        .add(storkTest("applied argument is not accidently bound to lambda parameter")
            .givenMocks("f", "x", "y", "z")
            .given("tuple(x)(y)(f) { f(x)(y) }")
            .when("tuple(y)(z)(f)")
            .thenReturned("f(y)(z)"));
  }
}
