package com.mikosik.stork.test.suites.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestListCount {
  public static Test testListCount() {
    return suite("list/count")
        .add(snippetSuite("count")
            .importing("lang/list/count/count")
            .importing("lang/list/limit")
            .importing("lang/list/each")
            .importing("lang/integer/add")
            .test("count.limit(5).each(add(97))", "abcde")
            .build())
        .add(snippetSuite("countFrom")
            .importing("lang/list/count/countFrom")
            .importing("lang/list/limit")
            .importing("lang/list/each")
            .importing("lang/integer/add")
            .test("countFrom(97).limit(5)", "abcde")
            .test("countFrom(-3).limit(5).each(add(100))", "abcde")
            .build())
        .add(snippetSuite("countDownFrom")
            .importing("lang/list/count/countDownFrom")
            .importing("lang/list/limit")
            .test("countDownFrom(101).limit(5)", "edcba")
            .build());
  }
}
