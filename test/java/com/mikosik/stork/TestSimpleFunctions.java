package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestSimpleFunctions {
  public static Suite testSimpleFunctions() {
    return suite("test simple functions")
        .add(storkTest("returning constant")
            .given("main{5}")
            .when("main")
            .thenReturned("5"))
        .add(storkTest("returning argument")
            .given("main(argument){argument}")
            .when("main(10)")
            .thenReturned("10"))
        .add(storkTest("forwarding argument and result")
            .given("identity(x){x}")
            .given("main(argument){identity(argument)}")
            .when("main(3)")
            .thenReturned("3"));
  }
}
