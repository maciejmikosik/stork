package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestTokenizer {
  public static Test testTokenizer() {
    return suite("legal variable names")
        .add(testLegalVariable("abc"))
        .add(testLegalVariable("x"))
        .add(testLegalVariable("abcD"))
        .add(testLegalVariable("ABC"))
        .add(testLegalVariable("AbCd"))
        .add(testLegalVariable("abc0"))
        .add(testLegalVariable("abc123"))
        .add(testLegalVariable("a1b2c3"))
        .add(testLegalVariable("aB1cD2"));
  }

  private static Test testLegalVariable(String name) {
    return suite(name)
        .add(programTest("as function name")
            .source("""
                main(stdin) { 'ok' }
                %s(x) { %s(x) }
                """
                .formatted(name, name))
            .stdout("ok"))
        .add(programTest("as function parameter")
            .source("""
                main(stdin) { 'ok' }
                f(%s) { %s }
                """
                .formatted(name, name))
            .stdout("ok"));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
