package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

public class TestBooleanLibrary {
  public static Suite testBooleanLibrary() {
    return suite("test boolean library")
        .add(suite("true/false")
            .add(testEqual("true(then)(else)", "then"))
            .add(testEqual("false(then)(else)", "else")))
        .add(suite("not")
            .add(testEqual("not(true)", "false"))
            .add(testEqual("not(false)", "true")))
        .add(suite("and")
            .add(testEqual("and(true)(true)", "true"))
            .add(testEqual("and(false)(true)", "false"))
            .add(testEqual("and(true)(false)", "false"))
            .add(testEqual("and(false)(false)", "false")))
        .add(suite("or")
            .add(testEqual("or(true)(true)", "true"))
            .add(testEqual("or(false)(true)", "true"))
            .add(testEqual("or(true)(false)", "true"))
            .add(testEqual("or(false)(false)", "false")));
  }

  private static Test testEqual(String expression, String expected) {
    return storkTest()
        .givenImported("boolean.stork")
        .given("then{5}")
        .given("else{7}")
        .when(expression)
        .thenReturned(expected);
  }
}
