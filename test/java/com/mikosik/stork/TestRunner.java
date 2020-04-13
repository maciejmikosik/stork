package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestRunner {
  public static Test testRunner() {
    return suite("compiling and running")
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
            .thenReturned("x"))
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
