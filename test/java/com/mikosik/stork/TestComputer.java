package com.mikosik.stork;

import static com.mikosik.stork.testing.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestComputer {
  public static Test testComputer() {
    return suite("compiling and computing")
        .add(storkTest("returning constant")
            .given("main{x}")
            .when("main")
            .thenReturned("x"))
        .add(storkTest("returning argument")
            .given("main(argument){argument}")
            .when("main(x)")
            .thenReturned("x"))
        .add(storkTest("forwarding argument and result")
            .given("identity(arg){arg}")
            .given("main(arg){identity(arg)}")
            .when("main(x)")
            .thenReturned("x"))
        .add(storkTest("application argument can be integer")
            .when("f(2)")
            .thenReturned("f(2)"))
        .add(storkTest("application argument can be lambda")
            .given("apply(f)(x) { f(x) }")
            .when("apply((x){x})(a)")
            .thenReturned("a"))
        .add(storkTest("noun is ignored when binding")
            .given("function(x) { x(2) }")
            .when("function(f)")
            .thenReturned("f(2)"))
        .add(storkTest("applied argument is not accidently bound to lambda parameter")
            .given("tuple(x)(y)(f) { f(x)(y) }")
            .when("tuple(y)(z)(f)")
            .thenReturned("f(y)(z)"));
  }
}
