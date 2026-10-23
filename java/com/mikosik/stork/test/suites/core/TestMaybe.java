package com.mikosik.stork.test.suites.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestMaybe {
  public static Test testMaybe() {
    return suite("maybe")
        .add(snippetSuite("something")
            .importing("lang/maybe/something")
            .test("something(1) ((x){x})(0)", 1)
            .build())
        .add(snippetSuite("nothing")
            .importing("lang/maybe/nothing")
            .test("nothing ((x){x})(0)", 0)
            .build())
        .add(snippetSuite("default")
            .importing("lang/maybe/default")
            .importing("lang/maybe/something")
            .importing("lang/maybe/nothing")
            .test("default(2)(something(1))", 1)
            .test("default(2)(nothing)", 2)
            .build());
  }
}
