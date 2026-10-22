package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.test.suites.language.TestCompilerProblems.testCompilerProblems;
import static com.mikosik.stork.test.suites.language.TestImporter.testImporter;
import static com.mikosik.stork.test.suites.language.TestImporterProblems.testImporterProblems;
import static com.mikosik.stork.test.suites.language.TestLinkerProblems.testLinkerProblems;
import static com.mikosik.stork.test.suites.language.TestParser.testParser;
import static com.mikosik.stork.test.suites.language.TestParserProblems.testParserProblems;
import static com.mikosik.stork.test.suites.language.TestTokenizer.testTokenizer;
import static com.mikosik.stork.test.suites.language.TestTokenizerProblems.testTokenizerProblems;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestLanguage {
  public static Test testLanguage() {
    return suite("language")
        .add(testTokenizer())
        .add(testParser())
        .add(testImporter())
        .add(suite("problems")
            .add(testImporterProblems())
            .add(testTokenizerProblems())
            .add(testParserProblems())
            .add(testCompilerProblems())
            .add(testLinkerProblems()));
  }
}
