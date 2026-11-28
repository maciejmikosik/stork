package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.test.suites.language.TestGeneralTokenizerMiscompilations.testGeneralTokenizerMiscompilations;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestTokenizerMiscompilations {
  public static Test testTokenizerMiscompilations() {
    return suite("test tokenizer miscompilations")
        .add(testGeneralTokenizerMiscompilations());
  }
}
