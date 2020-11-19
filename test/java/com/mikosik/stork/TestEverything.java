package com.mikosik.stork;

import static com.mikosik.stork.common.Strings.line;
import static com.mikosik.stork.testing.ComputerTest.computerTest;
import static com.mikosik.stork.testing.ModuleTest.testModule;
import static com.mikosik.stork.testing.ProgramTest.testProgramsIn;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import java.nio.file.Paths;

import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testProgramsIn(Paths.get("test/story")))
        .add(testComputer())
        .add(suite("modules")
            .add(testModule("literals.test.stork"))
            .add(testModule("function.test.stork"))
            .add(testModule("boolean.test.stork"))
            .add(testModule("optional.test.stork"))
            .add(testModule("integer.test.stork"))));
  }

  private static Test testComputer() {
    return suite("compiling and computing")
        .add(computerTest("returning constant", ""
            + line(" constant { value } ")
            + line(" when { constant }  ")
            + line(" then { value }     ")))
        .add(computerTest("returning argument", ""
            + line(" identity(x) { x }    ")
            + line(" when { identity(y) } ")
            + line(" then { y }           ")))
        .add(computerTest("forwarding argument and result", ""
            + line(" identity(x) { x }           ")
            + line(" function(x) { identity(x) } ")
            + line(" when { function(x) }        ")
            + line(" then { x }                  ")))
        .add(computerTest("application argument can be integer", ""
            + line(" identity(x) { x }    ")
            + line(" when { identity(2) } ")
            + line(" then { 2 }           ")))
        .add(computerTest("application argument can be lambda", ""
            + line(" apply(f)(x) { f(x) }      ")
            + line(" when { apply((x){x})(y) } ")
            + line(" then { y }                ")))
        .add(computerTest("integer is ignored when binding", ""
            + line(" apply2(f) { f(2) } ")
            + line(" when { apply2(g) } ")
            + line(" then { g(2) }      ")))
        .add(computerTest("applied argument is not accidently bound to lambda parameter", ""
            + line(" tuple(x)(y)(f) { f(x)(y) } ")
            + line(" when { tuple(y)(z)(f) }    ")
            + line(" then { f(y)(z) }           ")));
  }
}
