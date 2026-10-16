package com.mikosik.stork.test.cases.everything.core;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStreamCount {
  public static Test testStreamCount() {
    return suite("stream.count")
        .add(snippetSuite("count")
            .importing("lang/stream/count/count")
            .importing("lang/stream/limit")
            .importing("lang/stream/each")
            .importing("lang/integer/add")
            .test("count.limit(5).each(add(97))", "abcde")
            .build())
        .add(snippetSuite("countFrom")
            .importing("lang/stream/count/countFrom")
            .importing("lang/stream/limit")
            .importing("lang/stream/each")
            .importing("lang/integer/add")
            .test("countFrom(97).limit(5)", "abcde")
            .test("countFrom(-3).limit(5).each(add(100))", "abcde")
            .build())
        .add(snippetSuite("countDownFrom")
            .importing("lang/stream/count/countDownFrom")
            .importing("lang/stream/limit")
            .test("countDownFrom(101).limit(5)", "edcba")
            .build());
  }
}
