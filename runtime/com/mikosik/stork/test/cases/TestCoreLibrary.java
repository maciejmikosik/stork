package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static com.mikosik.stork.test.cases.TestBoolean.testBoolean;
import static com.mikosik.stork.test.cases.TestInteger.testInteger;
import static com.mikosik.stork.test.cases.TestStream.testStream;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.SnippetTest;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testBoolean())
        .add(testFunction())
        .add(testInteger())
        .add(testStream())
        .add(testOptional());
  }

  private static SnippetTest testFunction() {
    return snippetTest("function")
        .importing("lang.function.self")
        .importing("lang.function.flip")
        .importing("lang.function.compose")
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
        .importing("lang.optional.present")
        .importing("lang.optional.absent")
        .importing("lang.optional.else")
        .test("present(1) ((x){x})(2)", "1")
        .test("absent     ((x){x})(2)", "2")
        .test("else(2)(present(1))", "1")
        .test("else(2)(absent)    ", "2");
  }
}
