package com.mikosik.stork.test.cases;

import static com.mikosik.stork.test.SnippetSuite.snippetSuite;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestIntegerStream {
  public static Test testIntegerStream() {
    return suite("integer.stream")
        .add(snippetSuite("count")
            .importing("lang.integer.stream.count")
            .importing("lang.stream.limit")
            .importing("lang.stream.each")
            .importing("lang.integer.add")
            .test("each(add(97))(limit(5)(count))", "abcde"))
        .add(snippetSuite("countFrom")
            .importing("lang.integer.stream.countFrom")
            .importing("lang.stream.limit")
            .test("limit(5)(countFrom(97))", "abcde"))
        .add(snippetSuite("countTo")
            .importing("lang.integer.stream.countTo")
            .importing("lang.stream.each")
            .importing("lang.integer.add")
            .test("each(add(97))(countTo(5))", "abcde"));
  }
}
