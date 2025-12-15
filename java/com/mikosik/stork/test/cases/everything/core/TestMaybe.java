package com.mikosik.stork.test.cases.everything.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestMaybe {
  public static Test testMaybe() {
    return suite("maybe")
        .add(snippetSuite("something")
            .importing("lang.maybe.something")
            .test("something('s') ((x){x})('n')", "s")
            .build())
        .add(snippetSuite("nothing")
            .importing("lang.maybe.nothing")
            .test("nothing ((x){x})('n')", "n")
            .build())
        .add(snippetSuite("default")
            .importing("lang.maybe.default")
            .importing("lang.maybe.something")
            .importing("lang.maybe.nothing")
            .test("default('d')(something('s'))", "s")
            .test("default('d')(nothing)", "d")
            .build());
  }
}
