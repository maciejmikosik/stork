package com.mikosik.stork.test.cases.everything.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import java.math.BigInteger;

import org.quackery.Suite;
import org.quackery.Test;

public class TestInteger {
  public static Suite testInteger() {
    return suite("integer")
        .add(testIntegerCanonical())
        .add(testIntegerHuge())
        .add(testIntegerEqual())
        .add(testIntegerMoreThan())
        .add(testIntegerLessThan())
        .add(testIntegerAtLeast())
        .add(testIntegerAtMost())
        .add(testIntegerNegate())
        .add(testIntegerAdd())
        .add(testIntegerSubtract())
        .add(testIntegerIncrement())
        .add(testIntegerDecrement())
        .add(testIntegerMultiply())
        .add(testIntegerDivideBy())
        .add(testIntegerModulo())
        .add(testIntegerSign())
        .add(testIntegerAbsolute())
        .add(testIntegerMakeAtMost())
        .add(testIntegerMakeAtLeast())
        .add(testIntegerRelu())
        .add(testIntegerFormat())
        .add(testIntegerFormatBase());
  }

  private static Test testIntegerCanonical() {
    return snippetSuite("canonical")
        .test("+0 ", 0)
        .test("-0 ", 0)
        .test("+00", 0)
        .test(" 00", 0)
        .test("-00", 0)
        .test("+01", 1)
        .test(" 01", 1)
        .test("-01", -1)
        .build();
  }

  private static Test testIntegerHuge() {
    return snippetSuite("huge")
        .test("+1000000000000000000000000000000",
            big("1000000000000000000000000000000"))
        .test(" 1000000000000000000000000000000",
            big("1000000000000000000000000000000"))
        .test("-1000000000000000000000000000000",
            big("-1000000000000000000000000000000"))
        .build();
  }

  private static Test testIntegerEqual() {
    return snippetSuite("equal")
        .importing("lang/integer/equal")
        .importing("lang/integer/negate")
        .test("equal(-1)(-1)", true)
        .test("equal(-1)( 0)", false)
        .test("equal(-1)( 1)", false)
        .test("equal( 0)(-1)", false)
        .test("equal( 0)( 0)", true)
        .test("equal( 0)( 1)", false)
        .test("equal( 1)(-1)", false)
        .test("equal( 1)( 0)", false)
        .test("equal( 1)( 1)", true)
        .test("equal(negate(2))(negate(3))", false)
        .build();
  }

  private static Test testIntegerMoreThan() {
    return snippetSuite("moreThan")
        .importing("lang/integer/moreThan")
        .importing("lang/integer/negate")
        .test("moreThan(-1)(-1)", false)
        .test("moreThan(-1)( 0)", true)
        .test("moreThan(-1)( 1)", true)
        .test("moreThan( 0)(-1)", false)
        .test("moreThan( 0)( 0)", false)
        .test("moreThan( 0)( 1)", true)
        .test("moreThan( 1)(-1)", false)
        .test("moreThan( 1)( 0)", false)
        .test("moreThan( 1)( 1)", false)
        .test("moreThan(negate(3))(negate(4))", false)
        .build();
  }

  private static Test testIntegerLessThan() {
    return snippetSuite("lessThan")
        .importing("lang/integer/lessThan")
        .importing("lang/integer/negate")
        .test("lessThan(-1)(-1)", false)
        .test("lessThan(-1)( 0)", false)
        .test("lessThan(-1)( 1)", false)
        .test("lessThan( 0)(-1)", true)
        .test("lessThan( 0)( 0)", false)
        .test("lessThan( 0)( 1)", false)
        .test("lessThan( 1)(-1)", true)
        .test("lessThan( 1)( 0)", true)
        .test("lessThan( 1)( 1)", false)
        .test("lessThan(negate(3))(negate(4))", true)
        .build();
  }

  private static Test testIntegerAtLeast() {
    return snippetSuite("atLeast")
        .importing("lang/integer/atLeast")
        .importing("lang/integer/negate")
        .test("atLeast(-1)(-1)", true)
        .test("atLeast(-1)( 0)", true)
        .test("atLeast(-1)( 1)", true)
        .test("atLeast( 0)(-1)", false)
        .test("atLeast( 0)( 0)", true)
        .test("atLeast( 0)( 1)", true)
        .test("atLeast( 1)(-1)", false)
        .test("atLeast( 1)( 0)", false)
        .test("atLeast( 1)( 1)", true)
        .test("atLeast(negate(3))(negate(4))", false)
        .build();
  }

  private static Test testIntegerAtMost() {
    return snippetSuite("atMost")
        .importing("lang/integer/atMost")
        .importing("lang/integer/negate")
        .test("atMost(-1)(-1)", true)
        .test("atMost(-1)( 0)", false)
        .test("atMost(-1)( 1)", false)
        .test("atMost( 0)(-1)", true)
        .test("atMost( 0)( 0)", true)
        .test("atMost( 0)( 1)", false)
        .test("atMost( 1)(-1)", true)
        .test("atMost( 1)( 0)", true)
        .test("atMost( 1)( 1)", true)
        .test("atMost(negate(3))(negate(4))", true)
        .build();
  }

  private static Test testIntegerNegate() {
    return snippetSuite("negate")
        .importing("lang/integer/negate")
        .test("negate( 5)", -5)
        .test("negate(-3)", 3)
        .test("negate( 0)", 0)
        .test("negate(negate(3))", 3)
        .build();
  }

  private static Test testIntegerAdd() {
    return snippetSuite("add")
        .importing("lang/integer/add")
        .test("add(  2)( 3)", 5)
        .test("add( -1)( 1)", 0)
        .test("add(-10)(-5)", -15)
        .test("add(add(1)(2))(add(3)(4))", 10)
        .build();
  }

  private static Test testIntegerSubtract() {
    return snippetSuite("subtract")
        .importing("lang/integer/subtract")
        .test("subtract(  2)( 3)", 1)
        .test("subtract( -1)( 1)", 2)
        .test("subtract(-10)(-5)", 5)
        .test("subtract(subtract(1)(2))(subtract(3)(4))", 0)
        .build();
  }

  private static Test testIntegerIncrement() {
    return snippetSuite("increment")
        .importing("lang/integer/increment")
        .test("increment(-7)", -6)
        .test("increment( 0)", 1)
        .test("increment( 7)", 8)
        .test("increment(increment(0))", 2)
        .build();
  }

  private static Test testIntegerDecrement() {
    return snippetSuite("decrement")
        .importing("lang/integer/decrement")
        .test("decrement(-7)", -8)
        .test("decrement( 0)", -1)
        .test("decrement( 7)", 6)
        .test("decrement(decrement(0))", -2)
        .build();
  }

  private static Test testIntegerMultiply() {
    return snippetSuite("multiply")
        .importing("lang/integer/multiply")
        .test("multiply(-1)(-1)", 1)
        .test("multiply(-1)( 0)", 0)
        .test("multiply(-1)( 1)", -1)
        .test("multiply( 0)(-1)", 0)
        .test("multiply( 0)( 0)", 0)
        .test("multiply( 0)( 1)", 0)
        .test("multiply( 1)(-1)", -1)
        .test("multiply( 1)( 0)", 0)
        .test("multiply( 1)( 1)", 1)
        .test("multiply( 12)( 34)", 408)
        .test("multiply(-12)( 34)", -408)
        .test("multiply( 12)(-34)", -408)
        .test("multiply(-12)(-34)", 408)
        .test("multiply(multiply(2)(3))(multiply(4)(5))", 120)
        .build();
  }

  private static Test testIntegerDivideBy() {
    return snippetSuite("divideBy")
        .importing("lang/integer/divideBy")
        .test("divideBy(3)(-7)", -2)
        .test("divideBy(3)(-6)", -2)
        .test("divideBy(3)(-5)", -1)
        .test("divideBy(3)(-4)", -1)
        .test("divideBy(3)(-3)", -1)
        .test("divideBy(3)(-2)", 0)
        .test("divideBy(3)(-1)", 0)
        .test("divideBy(3)( 0)", 0)
        .test("divideBy(3)( 1)", 0)
        .test("divideBy(3)( 2)", 0)
        .test("divideBy(3)( 3)", 1)
        .test("divideBy(3)( 4)", 1)
        .test("divideBy(3)( 5)", 1)
        .test("divideBy(3)( 6)", 2)
        .test("divideBy(3)( 7)", 2)
        .test("divideBy(-3)(-7)", 2)
        .test("divideBy(-3)(-6)", 2)
        .test("divideBy(-3)(-5)", 1)
        .test("divideBy(-3)(-4)", 1)
        .test("divideBy(-3)(-3)", 1)
        .test("divideBy(-3)(-2)", 0)
        .test("divideBy(-3)(-1)", 0)
        .test("divideBy(-3)( 0)", 0)
        .test("divideBy(-3)( 1)", 0)
        .test("divideBy(-3)( 2)", 0)
        .test("divideBy(-3)( 3)", -1)
        .test("divideBy(-3)( 4)", -1)
        .test("divideBy(-3)( 5)", -1)
        .test("divideBy(-3)( 6)", -2)
        .test("divideBy(-3)( 7)", -2)
        .test("divideBy(divideBy(4)(12))(divideBy(2)(14))", 2)
        .build();
  }

  private static Test testIntegerModulo() {
    return snippetSuite("modulo")
        .importing("lang/integer/modulo")
        .test("modulo(3)(-7)", -1)
        .test("modulo(3)(-6)", 0)
        .test("modulo(3)(-5)", -2)
        .test("modulo(3)(-4)", -1)
        .test("modulo(3)(-3)", 0)
        .test("modulo(3)(-2)", -2)
        .test("modulo(3)(-1)", -1)
        .test("modulo(3)( 0)", 0)
        .test("modulo(3)( 1)", 1)
        .test("modulo(3)( 2)", 2)
        .test("modulo(3)( 3)", 0)
        .test("modulo(3)( 4)", 1)
        .test("modulo(3)( 5)", 2)
        .test("modulo(3)( 6)", 0)
        .test("modulo(3)( 7)", 1)
        .test("modulo(-3)(-7)", -1)
        .test("modulo(-3)(-6)", 0)
        .test("modulo(-3)(-5)", -2)
        .test("modulo(-3)(-4)", -1)
        .test("modulo(-3)(-3)", 0)
        .test("modulo(-3)(-2)", -2)
        .test("modulo(-3)(-1)", -1)
        .test("modulo(-3)( 0)", 0)
        .test("modulo(-3)( 1)", 1)
        .test("modulo(-3)( 2)", 2)
        .test("modulo(-3)( 3)", 0)
        .test("modulo(-3)( 4)", 1)
        .test("modulo(-3)( 5)", 2)
        .test("modulo(-3)( 6)", 0)
        .test("modulo(-3)( 7)", 1)
        .test("modulo(modulo(5)(9))(modulo(10)(17))", 3)
        .build();
  }

  private static Test testIntegerSign() {
    return snippetSuite("sign")
        .importing("lang/integer/sign")
        .test("sign(-21)", -1)
        .test("sign( -1)", -1)
        .test("sign(  0)", 0)
        .test("sign(  1)", 1)
        .test("sign( 21)", 1)
        .test("sign(sign(21))", 1)
        .build();
  }

  private static Test testIntegerAbsolute() {
    return snippetSuite("absolute")
        .importing("lang/integer/absolute")
        .test("absolute(-21)", 21)
        .test("absolute( -1)", 1)
        .test("absolute(  0)", 0)
        .test("absolute(  1)", 1)
        .test("absolute( 21)", 21)
        .test("absolute(absolute(21))", 21)
        .build();
  }

  private static Test testIntegerMakeAtMost() {
    return snippetSuite("makeAtMost")
        .importing("lang/integer/makeAtMost")
        .test("makeAtMost(7)(5)", 5)
        .test("makeAtMost(7)(9)", 7)
        .test("makeAtMost(7)(7)", 7)
        .test("makeAtMost(5)(makeAtMost(7)(9))", 5)
        .build();
  }

  private static Test testIntegerMakeAtLeast() {
    return snippetSuite("makeAtLeast")
        .importing("lang/integer/makeAtLeast")
        .test("makeAtLeast(7)(5)", 7)
        .test("makeAtLeast(7)(9)", 9)
        .test("makeAtLeast(7)(7)", 7)
        .test("makeAtLeast(9)(makeAtLeast(7)(5))", 9)
        .build();
  }

  private static Test testIntegerRelu() {
    return snippetSuite("relu")
        .importing("lang/integer/relu")
        .test("relu(-21)", 0)
        .test("relu( -1)", 0)
        .test("relu(  0)", 0)
        .test("relu(  1)", 1)
        .test("relu( 21)", 21)
        .test("relu(relu(21))", 21)
        .build();
  }

  private static Test testIntegerFormat() {
    return snippetSuite("format")
        .importing("lang/integer/format")
        .test("format(0)", "0")
        .test("format(1)", "1")
        .test("format(-1)", "-1")
        .test("format(1000000000)", "1000000000")
        .test("format(123456789123456789)", "123456789123456789")
        .test("format(-123456789123456789)", "-123456789123456789")
        .build();
  }

  private static Test testIntegerFormatBase() {
    return snippetSuite("formatBase")
        .importing("lang/integer/formatBase")
        .test("formatBase(2)(0)", "0")
        .test("formatBase(2)(1)", "1")
        .test("formatBase(2)(10)", "1010")
        .test("formatBase(2)(-1)", "-1")
        .test("formatBase(2)(-10)", "-1010")
        .test("formatBase(16)(1)", "1")
        .test("formatBase(16)(255)", "FF")
        .test("formatBase(16)(-1)", "-1")
        .test("formatBase(16)(-255)", "-FF")
        .build();
  }

  private static BigInteger big(String string) {
    return new BigInteger(string);
  }
}
