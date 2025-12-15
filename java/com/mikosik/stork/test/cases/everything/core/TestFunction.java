package com.mikosik.stork.test.cases.everything.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestFunction {
  public static Test testFunction() {
    return suite("function")
        .add(snippetSuite("identity")
            .importing("lang.function.identity")
            .test("identity('0')", "0")
            .test("identity('1')", "1")
            .build())
        .add(snippetSuite("constant")
            .importing("lang.function.constant")
            .test("constant('1')('1')", "1")
            .test("constant('1')('2')", "1")
            .test("constant('1')('3')", "1")
            .build())
        .add(snippetSuite("flip")
            .importing("lang.function.flip")
            .test("flip(      (x)(y){x}  )('1')('2')", "2")
            .test("flip(flip( (x)(y){x} ))('1')('2')", "1")
            .build())
        .add(snippetSuite("compose")
            .importing("lang.function.compose")
            .test("compose((x){ x })((x){ x })('0')", "0")
            .test("compose((x){'1'})((x){ x })('0')", "1")
            .test("compose((x){ x })((x){'2'})('0')", "2")
            .test("compose((x){'1'})((x){'2'})('0')", "1")
            .build());
  }
}
