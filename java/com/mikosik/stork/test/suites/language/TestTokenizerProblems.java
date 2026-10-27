package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.test.suites.language.TestGeneralTokenizerProblems.testGeneralTokenizerProblems;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestTokenizerProblems {
  public static Test testTokenizerProblems() {
    return suite("test tokenizer problems")
        .add(testGeneralTokenizerProblems());
  }
}
