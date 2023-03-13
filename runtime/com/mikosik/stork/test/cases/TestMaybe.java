package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetTest.snippetTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestMaybe {
  public static Test testMaybe() {
    return suite("maybe")
        .add(snippetTest("present")
            .importing("lang.maybe.present")
            .test("present(1)((x){x})(2)", "1"))
        .add(snippetTest("absent")
            .importing("lang.maybe.absent")
            .test("absent((x){x})(2)", "2"))
        .add(snippetTest("absent")
            .importing("lang.maybe.else")
            .importing("lang.maybe.present")
            .importing("lang.maybe.absent")
            .test("else(2)(present(1))", "1")
            .test("else(2)(absent)", "2"));
  }
}
