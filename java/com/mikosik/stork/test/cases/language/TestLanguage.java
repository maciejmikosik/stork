package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.test.cases.language.TestCompilerProblems.testCompilerProblems;
import static com.mikosik.stork.test.cases.language.TestSyntax.testSyntax;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestLanguage {
  public static Test testLanguage() {
    return suite("language")
        .add(testSyntax())
        .add(testCompilerProblems());
  }
}
