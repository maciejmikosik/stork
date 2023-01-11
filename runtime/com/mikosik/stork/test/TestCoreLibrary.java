package com.mikosik.stork.test;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestCoreLibrary {
  public static Test testCoreLibrary() {
    return suite("core library")
        .add(testFunction())
        .add(suite("integer")
            .add(testIntegerCanonical())
            .add(testIntegerHuge())
            .add(testIntegerEqual())
            .add(testIntegerNegate())
            .add(testIntegerAdd())
            .add(testIntegerMoreThan())
            .add(testIntegerEager()))
        .add(testOptional());
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

  private static SnippetTest testIntegerCanonical() {
    return snippetTest("canonical")
        .test("+0 ", " 0")
        .test("-0 ", " 0")
        .test("+00", " 0")
        .test(" 00", " 0")
        .test("-00", " 0")
        .test("+01", " 1")
        .test(" 01", " 1")
        .test("-01", "-1");
  }

  private static Test testIntegerHuge() {
    return snippetTest("huge")
        .test("+1000000000000000000000000000000",
            "  +1000000000000000000000000000000")
        .test(" 1000000000000000000000000000000",
            "   1000000000000000000000000000000")
        .test("-1000000000000000000000000000000",
            "  -1000000000000000000000000000000");
  }

  private static Test testIntegerEqual() {
    return snippetTest("equal")
        .importing("stork.boolean.true")
        .importing("stork.boolean.false")
        .importing("stork.integer.equal")
        .test("equal(-1)(-1)", "true")
        .test("equal(-1)( 0)", "false")
        .test("equal(-1)( 1)", "false")
        .test("equal( 0)(-1)", "false")
        .test("equal( 0)( 0)", "true")
        .test("equal( 0)( 1)", "false")
        .test("equal( 1)(-1)", "false")
        .test("equal( 1)( 0)", "false")
        .test("equal( 1)( 1)", "true");
  }

  private static Test testIntegerNegate() {
    return snippetTest("negate")
        .importing("stork.integer.negate")
        .test("negate( 5)", "-5")
        .test("negate(-3)", " 3")
        .test("negate( 0)", " 0");
  }

  private static Test testIntegerAdd() {
    return snippetTest("add")
        .importing("stork.integer.add")
        .test("add(  2)( 3)", "  5")
        .test("add( -1)( 1)", "  0")
        .test("add(-10)(-5)", "-15");
  }

  private static Test testIntegerMoreThan() {
    return snippetTest("moreThan")
        .importing("stork.boolean.true")
        .importing("stork.boolean.false")
        .importing("stork.integer.moreThan")
        .test("moreThan(-1)(-1)", "false")
        .test("moreThan(-1)( 0)", "true")
        .test("moreThan(-1)( 1)", "true")
        .test("moreThan( 0)(-1)", "false")
        .test("moreThan( 0)( 0)", "false")
        .test("moreThan( 0)( 1)", "true")
        .test("moreThan( 1)(-1)", "false")
        .test("moreThan( 1)( 0)", "false")
        .test("moreThan( 1)( 1)", "false");
  }

  private static Test testIntegerEager() {
    return snippetTest("eager")
        .importing("stork.boolean.true")
        .importing("stork.boolean.false")
        .importing("stork.integer.equal")
        .importing("stork.integer.negate")
        .importing("stork.integer.add")
        .importing("stork.integer.moreThan")
        .test("equal(negate(2))(negate(3))", "false")
        .test("add(add(1)(2))(add(3)(4))", "10")
        .test("moreThan(negate(3))(negate(4))", "false")
        .test("negate(negate(3))", "3");
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
