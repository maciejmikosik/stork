package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestStreamInteger {
  public static Test testStreamInteger() {
    return suite("stream.integer")
        .add(snippetSuite("count")
            .importing("lang.stream.integer.count")
            .importing("lang.stream.limit")
            .importing("lang.stream.each")
            .importing("lang.integer.add")
            .test("each(add(97))(limit(5)(count))", "abcde"))
        .add(snippetSuite("countFrom")
            .importing("lang.stream.integer.countFrom")
            .importing("lang.stream.limit")
            .test("limit(5)(countFrom(97))", "abcde"))
        .add(snippetSuite("countTo")
            .importing("lang.stream.integer.countTo")
            .importing("lang.stream.each")
            .importing("lang.integer.add")
            .test("each(add(97))(countTo(5))", "abcde"));
  }
}
