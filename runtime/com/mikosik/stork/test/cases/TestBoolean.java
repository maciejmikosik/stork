package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestBoolean {
  public static Test testBoolean() {
    return suite("boolean")
        .add(snippetTest("constructor")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .test("true (0)(1)", "0")
            .test("false(0)(1)", "1"))
        .add(snippetTest("not")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.not")
            .test("not(true)", "false")
            .test("not(false)", "true"))
        .add(snippetTest("and")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.and")
            .test("and(true) (true) ", "true")
            .test("and(true) (false)", "false")
            .test("and(false)(true) ", "false")
            .test("and(false)(false)", "false"))
        .add(snippetTest("or")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.or")
            .test("or(true) (true) ", "true")
            .test("or(true) (false)", "true")
            .test("or(false)(true) ", "true")
            .test("or(false)(false)", "false"));
  }
}
