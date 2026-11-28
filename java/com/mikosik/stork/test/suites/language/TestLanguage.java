package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.test.suites.language.TestImporter.testImporter;
import static com.mikosik.stork.test.suites.language.TestImporterMiscompilations.testImporterMiscompilations;
import static com.mikosik.stork.test.suites.language.TestLinkerMiscompilations.testLinkerMiscompilations;
import static com.mikosik.stork.test.suites.language.TestMiscompilations.testCompilerMiscompilations;
import static com.mikosik.stork.test.suites.language.TestParser.testParser;
import static com.mikosik.stork.test.suites.language.TestParserMiscompilations.testParserMiscompilations;
import static com.mikosik.stork.test.suites.language.TestStringLiteral.testStringLiteral;
import static com.mikosik.stork.test.suites.language.TestTokenizer.testTokenizer;
import static com.mikosik.stork.test.suites.language.TestTokenizerMiscompilations.testTokenizerMiscompilations;
import static org.quackery.Suite.suite;

import org.quackery.Test;

public class TestLanguage {
  public static Test testLanguage() {
    return suite("language")
        .add(testStringLiteral())
        .add(testTokenizer())
        .add(testParser())
        .add(testImporter())
        .add(suite("miscompilations")
            .add(testImporterMiscompilations())
            .add(testTokenizerMiscompilations())
            .add(testParserMiscompilations())
            .add(testCompilerMiscompilations())
            .add(testLinkerMiscompilations()));
  }
}
