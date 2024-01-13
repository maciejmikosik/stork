package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestBoolean {
  public static Test testBoolean() {
    return suite("boolean")
        .add(snippetSuite("constructor")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .test("true (0)(1)", 0)
            .test("false(0)(1)", 1))
        .add(snippetSuite("equal")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.equal")
            .test("equal(true) (true) ", true)
            .test("equal(true) (false)", false)
            .test("equal(false)(true) ", false)
            .test("equal(false)(false)", true))
        .add(snippetSuite("not")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.not")
            .test("not(true)", false)
            .test("not(false)", true))
        .add(snippetSuite("and")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.and")
            .test("and(true) (true) ", true)
            .test("and(true) (false)", false)
            .test("and(false)(true) ", false)
            .test("and(false)(false)", false))
        .add(snippetSuite("or")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.or")
            .test("or(true) (true) ", true)
            .test("or(true) (false)", true)
            .test("or(false)(true) ", true)
            .test("or(false)(false)", false))
        .add(snippetSuite("xor")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.xor")
            .test("xor(true) (true) ", false)
            .test("xor(true) (false)", true)
            .test("xor(false)(true) ", true)
            .test("xor(false)(false)", false))
        .add(snippetSuite("format")
            .importing("lang.boolean.true")
            .importing("lang.boolean.false")
            .importing("lang.boolean.format")
            .test("format(true)", "true")
            .test("format(false)", "false"));
  }
}
