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
            .test("each(add(97))(limit(5)(count))", "abcde")
            .build())
        .add(snippetSuite("countFrom")
            .importing("lang/stream/count/countFrom")
            .importing("lang/stream/limit")
            .importing("lang/stream/each")
            .importing("lang/integer/add")
            .test("limit(5)(countFrom(97))", "abcde")
            .test("each(add(97))(limit(5)(countFrom(0)))", "abcde")
            .test("each(add(98))(limit(5)(countFrom(-1)))", "abcde")
            .build())
        .add(snippetSuite("countTo")
            .importing("lang/stream/count/countTo")
            .importing("lang/stream/each")
            .importing("lang/integer/add")
            .test("each(add(97))(countTo(5))", "abcde")
            .test("each(add(97))(countTo(0))", "")
            .test("each(add(97))(countTo(-1))", "")
            .build())
        .add(snippetSuite("countDownFrom")
            .importing("lang/stream/count/countDownFrom")
            .importing("lang/stream/each")
            .importing("lang/integer/add")
            .test("each(add(97))(countDownFrom(5))", "edcba")
            .test("each(add(97))(countDownFrom(0))", "")
            .test("each(add(97))(countDownFrom(-1))", "")
            .build());
  }
}
