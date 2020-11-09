package com.mikosik.stork;

import static com.mikosik.stork.common.Strings.line;
import static com.mikosik.stork.testing.ComputerTest.computerTest;
import static com.mikosik.stork.testing.ModuleTest.testModule;
import static com.mikosik.stork.testing.ProgramTest.programTest;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.timeout;

import org.quackery.Suite;
import org.quackery.Test;

public class TestEverything {
  public static Test testEverything() {
    return timeout(0.1, suite("test everything")
        .add(testComputer())
        .add(testProgram())
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

  private static Suite testProgram() {
    return suite("program")
        .add(programTest("uses string literal", ""
            + line(" main {  ")
            + line("   'abc' ")
            + line(" }       "),
            "abc"))
        .add(programTest("uses core function", ""
            + line(" build {                                         ")
            + line("   module                                        ")
            + line("     (importAs('append')('stork.stream.append')) ")
            + line(" }                                               ")
            + line("                                                 ")
            + line(" main {                                          ")
            + line("   append('!')('Hello World')                    ")
            + line(" }                                               "),
            "Hello World!"))
        .add(programTest("handles character codes", ""
            + line(" build {                                         ")
            + line("   module                                        ")
            + line("     (importAs('append')('stork.stream.append')) ")
            + line("     (importAs('single')('stork.stream.single')) ")
            + line(" }                                               ")
            + line("                                                 ")
            + line(" main {                                          ")
            + line("   append(single(33))('Hello World')             ")
            + line(" }                                               "),
            "Hello World!"));
  }
}
