package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.common.io.AsciiSubset.asciiSubset;
import static com.mikosik.stork.compile.err.Problems.illegalCharacterInCode;
import static com.mikosik.stork.compile.err.Problems.malformedWord;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.common.io.AsciiSubset;
import com.mikosik.stork.test.ProgramTest;

public class TestGeneralTokenizerProblems {
  public static Test testGeneralTokenizerProblems() {
    return suite("tokenizer")
        .add(reportsIllegalCharacters())
        .add(reportsMalformedTokens());
  }

  private static Suite reportsIllegalCharacters() {
    return suite("reports illegal character in source code")
        .addAll(rangeClosed(0, 255)
            .mapToObj(value -> Byte.valueOf((byte) value))
            .filter(character -> !legalCharacters.contains(character))
            .map(TestGeneralTokenizerProblems::reportsIllegal)
            .toList());
  }

  private static final AsciiSubset legalCharacters = asciiSubset()
      .addLetters()
      .addNumerics()
      .addWhitespaces()
      .add("(){}.\"")
      .build();

  private static Test reportsIllegal(byte character) {
    return programTest("[%d]".formatted(character))
        .source(sourceCodeWith(character))
        .expect(illegalCharacterInCode()
            .in(root)
            .in(variable("main"))
            .object(character));
  }

  private static byte[] sourceCodeWith(byte character) {
    var bytes = "main(stdin) { ? }".getBytes(UTF_8);
    byte questionMark = '?';
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] == questionMark) {
        bytes[i] = character;
      }
    }
    return bytes;
  }

  private static Test reportsMalformedTokens() {
    return suite("reports malformed tokens")
        .add(suite("of integer literals")
            .add(reportsMalformed("123x"))
            .add(reportsMalformed("+123x"))
            .add(reportsMalformed("123-"))
            .add(reportsMalformed("1x23"))
            .add(reportsMalformed("1+23"))
            .add(reportsMalformed("+-"))
            .add(reportsMalformed("+123-")))
        .add(suite("of identifiers")
            .add(reportsMalformed("abc+"))
            .add(reportsMalformed("abc-"))
            .add(reportsMalformed("ab+c"))
            .add(reportsMalformed("ab-c")));
  }

  private static Test reportsMalformed(String token) {
    return programTest(token)
        .source("main(stdin) { %s }".formatted(token))
        .expect(malformedWord()
            .in(root)
            .in(variable("main"))
            .object(token));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
