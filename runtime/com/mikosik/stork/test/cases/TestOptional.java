package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestOptional {
  public static Test testOptional() {
    return suite("optional")
        .add(snippetTest("present")
            .importing("lang.optional.present")
            .test("present(1)((x){x})(2)", "1"))
        .add(snippetTest("absent")
            .importing("lang.optional.absent")
            .test("absent((x){x})(2)", "2"))
        .add(snippetTest("absent")
            .importing("lang.optional.else")
            .importing("lang.optional.present")
            .importing("lang.optional.absent")
            .test("else(2)(present(1))", "1")
            .test("else(2)(absent)", "2"));
  }
}
