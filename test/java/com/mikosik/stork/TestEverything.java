package com.mikosik.stork;

import static com.mikosik.stork.testing.ComputerTest.computerTest;
import static com.mikosik.stork.testing.ModuleTest.testModule;
import static com.mikosik.stork.testing.ProgramTest.programTest;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testComputer())
        .add(suite("program")
            .add(programTest("'abc'", "abc"))
            .add(programTest("append('!')('Hello World')", "Hello World!"))
            .add(programTest("append(single(33))('Hello World')", "Hello World!")))
        .add(suite("modules")
            .add(testModule("function.stork", "testFunction"))
            .add(testModule("boolean.stork", "testBoolean"))
            .add(testModule("optional.stork", "testOptional"))
            .add(testModule("integer.stork", "testInteger"))
            .add(testModule("stream.stork", "testStream"))));
  }

  private static Test testComputer() {
    return suite("compiling and computing")
        .add(computerTest("returning constant", ""
            + " constant { value } "
            + " when { constant }  "
            + " then { value }     "))
        .add(computerTest("returning argument", ""
            + " identity(x) { x }    "
            + " when { identity(y) } "
            + " then { y }           "))
        .add(computerTest("forwarding argument and result", ""
            + " identity(x) { x }           "
            + " function(x) { identity(x) } "
            + " when { function(x) }        "
            + " then { x }                  "))
        .add(computerTest("application argument can be integer", ""
            + " identity(x) { x }    "
            + " when { identity(2) } "
            + " then { 2 }           "))
        .add(computerTest("application argument can be lambda", ""
            + " apply(f)(x) { f(x) }      "
            + " when { apply((x){x})(y) } "
            + " then { y }                "))
        .add(computerTest("integer is ignored when binding", ""
            + " apply2(f) { f(2) } "
            + " when { apply2(g) } "
            + " then { g(2) }      "))
        .add(computerTest("applied argument is not accidently bound to lambda parameter", ""
            + " tuple(x)(y)(f) { f(x)(y) } "
            + " when { tuple(y)(z)(f) }    "
            + " then { f(y)(z) }           "));
  }
}
