package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static com.mikosik.stork.test.cases.TestInteger.testInteger;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.SnippetTest;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testBoolean())
        .add(testFunction())
        .add(testInteger())
        .add(testOptional());
  }

  private static SnippetTest testBoolean() {
    return snippetTest("boolean")
        .importing("stork.boolean.true")
        .importing("stork.boolean.false")
        .importing("stork.boolean.not")
        .importing("stork.boolean.and")
        .importing("stork.boolean.or")
        .test("true (0)(1)", "0")
        .test("false(0)(1)", "1")
        .test("not(true)", "false")
        .test("not(false)", "true")
        .test("and(true) (true) ", "true")
        .test("and(true) (false)", "false")
        .test("and(false)(true) ", "false")
        .test("and(false)(false)", "false")
        .test("or(true) (true) ", "true")
        .test("or(true) (false)", "true")
        .test("or(false)(true) ", "true")
        .test("or(false)(false)", "false");
  }

  private static SnippetTest testFunction() {
    return snippetTest("function")
        .importing("stork.function.self")
        .importing("stork.function.flip")
        .importing("stork.function.compose")
        .test("self(0)", "0")
        .test("self(1)", "1")
        .test("flip(      (x)(y){x}  )(1)(2)", "2")
        .test("flip(flip( (x)(y){x} ))(1)(2)", "1")
        .test("compose((x){x})((x){x})(0)", "0")
        .test("compose((x){1})((x){x})(0)", "1")
        .test("compose((x){x})((x){2})(0)", "2")
        .test("compose((x){1})((x){2})(0)", "1");
  }

  private static Test testOptional() {
    return snippetTest("optional")
        .importing("stork.optional.present")
        .importing("stork.optional.absent")
        .importing("stork.optional.else")
        .test("present(1) ((x){x})(2)", "1")
        .test("absent     ((x){x})(2)", "2")
        .test("else(2)(present(1))", "1")
        .test("else(2)(absent)    ", "2");
  }
}
