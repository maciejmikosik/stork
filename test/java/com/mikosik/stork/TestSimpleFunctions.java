package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestSimpleFunctions {
  public static Suite testSimpleFunctions() {
    return suite("test simple functions")
        .add(storkTest("returning constant")
            .givenMocks("x")
            .given("main{x}")
            .when("main")
            .thenReturned("x"))
        .add(storkTest("returning argument")
            .givenMocks("x")
            .given("main(argument){argument}")
            .when("main(x)")
            .thenReturned("x"))
        .add(storkTest("forwarding argument and result")
            .givenMocks("x")
            .given("identity(arg){arg}")
            .given("main(arg){identity(arg)}")
            .when("main(x)")
            .thenReturned("x"));
  }
}
