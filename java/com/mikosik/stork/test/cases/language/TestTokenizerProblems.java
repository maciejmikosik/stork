package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.test.cases.language.TestGeneralTokenizerProblems.testGeneralTokenizerProblems;
import static com.mikosik.stork.test.cases.language.TestStringLiteral.testStringLiteral;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestTokenizerProblems {
  public static Test testTokenizerProblems() {
    return suite("test tokenizer problems")
        .add(testGeneralTokenizerProblems())
        .add(testStringLiteral());
  }
}
