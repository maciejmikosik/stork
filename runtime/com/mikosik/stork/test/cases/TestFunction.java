package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestFunction {
  public static Test testFunction() {
    return suite("function")
        .add(snippetTest("identity")
            .importing("lang.function.identity")
            .test("identity(0)", "0")
            .test("identity(1)", "1"))
        .add(snippetTest("flip")
            .importing("lang.function.flip")
            .test("flip(      (x)(y){x}  )(1)(2)", "2")
            .test("flip(flip( (x)(y){x} ))(1)(2)", "1"))
        .add(snippetTest("compose")
            .importing("lang.function.compose")
            .test("compose((x){x})((x){x})(0)", "0")
            .test("compose((x){1})((x){x})(0)", "1")
            .test("compose((x){x})((x){2})(0)", "2")
            .test("compose((x){1})((x){2})(0)", "1"));
  }
}
