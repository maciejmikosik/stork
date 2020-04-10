package com.mikosik.stork;

import static com.mikosik.stork.StorkTest.storkTest;
import static org.quackery.Suite.suite;

import org.quackery.Suite;

public class TestBooleanLibrary {
  public static Suite testBooleanLibrary() {
    StorkTest test = storkTest()
        .givenImported("boolean.stork")
        .given("then{5}")
        .given("else{7}");

    return suite("test boolean library")
        .add(suite("true/false")
            .add(test
                .when("true(then)(else)")
                .thenReturned("then"))
            .add(test
                .when("false(then)(else)")
                .thenReturned("else")))
        .add(suite("not")
            .add(test
                .when("not(true)")
                .thenReturned("false"))
            .add(test
                .when("not(false)")
                .thenReturned("true")))
        .add(suite("and")
            .add(test
                .when("and(true)(true)")
                .thenReturned("true"))
            .add(test
                .when("and(false)(true)")
                .thenReturned("false"))
            .add(test
                .when("and(true)(false)")
                .thenReturned("false"))
            .add(test
                .when("and(false)(false)")
                .thenReturned("false")))
        .add(suite("or")
            .add(test
                .when("or(true)(true)")
                .thenReturned("true"))
            .add(test
                .when("or(false)(true)")
                .thenReturned("true"))
            .add(test
                .when("or(true)(false)")
                .thenReturned("true"))
            .add(test
                .when("or(false)(false)")
                .thenReturned("false")));
  }
}
