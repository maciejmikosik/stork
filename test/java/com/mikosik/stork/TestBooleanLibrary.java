package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestBooleanLibrary {
  public static Suite testBooleanLibrary() {
    return suite("test boolean library")
        .add(suite("true/false")
            .add(storkTest("true(then)(else) = then")
                .givenImported("boolean.stork")
                .when("true(1)(2)")
                .thenReturned("1"))
            .add(storkTest("false(then)(else) = else")
                .givenImported("boolean.stork")
                .when("false(1)(2)")
                .thenReturned("2")))
        .add(suite("not")
            .add(storkTest("not(true) = false")
                .givenImported("boolean.stork")
                .when("not(true)")
                .thenReturned("false"))
            .add(storkTest("not(false) = true")
                .givenImported("boolean.stork")
                .when("not(false)")
                .thenReturned("true")))
        .add(suite("and")
            .add(storkTest("and(true)(true) = true")
                .givenImported("boolean.stork")
                .when("and(true)(true)")
                .thenReturned("true"))
            .add(storkTest("and(false)(true) = false")
                .givenImported("boolean.stork")
                .when("and(false)(true)")
                .thenReturned("false"))
            .add(storkTest("and(true)(false) = false")
                .givenImported("boolean.stork")
                .when("and(true)(false)")
                .thenReturned("false"))
            .add(storkTest("and(false)(false) = false")
                .givenImported("boolean.stork")
                .when("and(false)(false)")
                .thenReturned("false")))
        .add(suite("or")
            .add(storkTest("or(true)(true) = true")
                .givenImported("boolean.stork")
                .when("or(true)(true)")
                .thenReturned("true"))
            .add(storkTest("or(false)(true) = true")
                .givenImported("boolean.stork")
                .when("or(false)(true)")
                .thenReturned("true"))
            .add(storkTest("or(true)(false) = true")
                .givenImported("boolean.stork")
                .when("or(true)(false)")
                .thenReturned("true"))
            .add(storkTest("or(false)(false) = false")
                .givenImported("boolean.stork")
                .when("or(false)(false)")
                .thenReturned("false")));
  }
}
