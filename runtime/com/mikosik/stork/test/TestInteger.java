package com.mikosik.stork.test;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

public class TestInteger {
  public static Suite testInteger() {
    return suite("integer")
        .add(testIntegerCanonical())
        .add(testIntegerHuge())
        .add(testIntegerEqual())
        .add(testIntegerMoreThan())
        .add(testIntegerNegate())
        .add(testIntegerAdd())
        .add(testIntegerMultiply())
        .add(testIntegerDivideBy())
        .add(testIntegerModulo())
        .add(testIntegerSignum())
        .add(testIntegerAbsolute())
        .add(testIntegerRelu())
        .add(testIntegerEager());
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

  private static Test testIntegerMultiply() {
    return snippetTest("multiply")
        .importing("stork.integer.multiply")
        .test("multiply(-1)(-1)", " 1")
        .test("multiply(-1)( 0)", " 0")
        .test("multiply(-1)( 1)", "-1")
        .test("multiply( 0)(-1)", " 0")
        .test("multiply( 0)( 0)", " 0")
        .test("multiply( 0)( 1)", " 0")
        .test("multiply( 1)(-1)", "-1")
        .test("multiply( 1)( 0)", " 0")
        .test("multiply( 1)( 1)", " 1")
        .test("multiply( 12)( 34)", " 408")
        .test("multiply(-12)( 34)", "-408")
        .test("multiply( 12)(-34)", "-408")
        .test("multiply(-12)(-34)", " 408")
        .test("multiply(multiply(2)(3))(multiply(4)(5))", "120");
  }

  private static Test testIntegerDivideBy() {
    return snippetTest("divideBy")
        .importing("stork.integer.divideBy")
        .test("divideBy(3)(-7)", "-2")
        .test("divideBy(3)(-6)", "-2")
        .test("divideBy(3)(-5)", "-1")
        .test("divideBy(3)(-4)", "-1")
        .test("divideBy(3)(-3)", "-1")
        .test("divideBy(3)(-2)", " 0")
        .test("divideBy(3)(-1)", " 0")
        .test("divideBy(3)( 0)", " 0")
        .test("divideBy(3)( 1)", " 0")
        .test("divideBy(3)( 2)", " 0")
        .test("divideBy(3)( 3)", " 1")
        .test("divideBy(3)( 4)", " 1")
        .test("divideBy(3)( 5)", " 1")
        .test("divideBy(3)( 6)", " 2")
        .test("divideBy(3)( 7)", " 2")
        .test("divideBy(-3)(-7)", " 2")
        .test("divideBy(-3)(-6)", " 2")
        .test("divideBy(-3)(-5)", " 1")
        .test("divideBy(-3)(-4)", " 1")
        .test("divideBy(-3)(-3)", " 1")
        .test("divideBy(-3)(-2)", " 0")
        .test("divideBy(-3)(-1)", " 0")
        .test("divideBy(-3)( 0)", " 0")
        .test("divideBy(-3)( 1)", " 0")
        .test("divideBy(-3)( 2)", " 0")
        .test("divideBy(-3)( 3)", "-1")
        .test("divideBy(-3)( 4)", "-1")
        .test("divideBy(-3)( 5)", "-1")
        .test("divideBy(-3)( 6)", "-2")
        .test("divideBy(-3)( 7)", "-2")
        .test("divideBy(divideBy(4)(12))(divideBy(2)(14))", "2");
  }

  private static Test testIntegerModulo() {
    return snippetTest("modulo")
        .importing("stork.integer.modulo")
        .test("modulo(3)(-7)", "-1")
        .test("modulo(3)(-6)", " 0")
        .test("modulo(3)(-5)", "-2")
        .test("modulo(3)(-4)", "-1")
        .test("modulo(3)(-3)", " 0")
        .test("modulo(3)(-2)", "-2")
        .test("modulo(3)(-1)", "-1")
        .test("modulo(3)( 0)", " 0")
        .test("modulo(3)( 1)", " 1")
        .test("modulo(3)( 2)", " 2")
        .test("modulo(3)( 3)", " 0")
        .test("modulo(3)( 4)", " 1")
        .test("modulo(3)( 5)", " 2")
        .test("modulo(3)( 6)", " 0")
        .test("modulo(3)( 7)", " 1")
        .test("modulo(-3)(-7)", "-1")
        .test("modulo(-3)(-6)", " 0")
        .test("modulo(-3)(-5)", "-2")
        .test("modulo(-3)(-4)", "-1")
        .test("modulo(-3)(-3)", " 0")
        .test("modulo(-3)(-2)", "-2")
        .test("modulo(-3)(-1)", "-1")
        .test("modulo(-3)( 0)", " 0")
        .test("modulo(-3)( 1)", " 1")
        .test("modulo(-3)( 2)", " 2")
        .test("modulo(-3)( 3)", " 0")
        .test("modulo(-3)( 4)", " 1")
        .test("modulo(-3)( 5)", " 2")
        .test("modulo(-3)( 6)", " 0")
        .test("modulo(-3)( 7)", " 1")
        .test("modulo(modulo(5)(9))(modulo(10)(17))", "3");
  }

  private static Test testIntegerSignum() {
    return snippetTest("signum")
        .importing("stork.integer.signum")
        .test("signum(-21)", "-1")
        .test("signum( -1)", "-1")
        .test("signum(  0)", " 0")
        .test("signum(  1)", " 1")
        .test("signum( 21)", " 1")
        .test("signum(signum(21))", " 1");
  }

  private static Test testIntegerAbsolute() {
    return snippetTest("absolute")
        .importing("stork.integer.absolute")
        .test("absolute(-21)", "21")
        .test("absolute( -1)", " 1")
        .test("absolute(  0)", " 0")
        .test("absolute(  1)", " 1")
        .test("absolute( 21)", "21")
        .test("absolute(absolute(21))", "21");
  }

  private static Test testIntegerRelu() {
    return snippetTest("relu")
        .importing("stork.integer.relu")
        .test("relu(-21)", " 0")
        .test("relu( -1)", " 0")
        .test("relu(  0)", " 0")
        .test("relu(  1)", " 1")
        .test("relu( 21)", "21")
        .test("relu(relu(21))", "21");
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
}
