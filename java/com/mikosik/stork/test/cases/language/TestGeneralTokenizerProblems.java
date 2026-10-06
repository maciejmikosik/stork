package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isWhitespace;
import static com.mikosik.stork.compile.err.Problems.illegalCharacterInCode;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestGeneralTokenizerProblems {
  public static Test testGeneralTokenizerProblems() {
    return suite("tokenizer")
        .add(reportsIllegalCharacters());
  }

  private static Suite reportsIllegalCharacters() {
    return suite("reports illegal character in source code")
        .addAll(rangeClosed(0, 255)
            .mapToObj(value -> Byte.valueOf((byte) value))
            .filter(character -> !isLegal(character))
            .map(TestGeneralTokenizerProblems::reportsIllegal)
            .toList());
  }

  private static Test reportsIllegal(byte character) {
    return programTest("[%d]".formatted(character))
        .source(sourceCodeWith(character))
        .expect(illegalCharacterInCode()
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

  private static boolean isLegal(byte character) {
    return isAlphanumeric(character)
        || isWhitespace(character)
        || "(){}.\"".indexOf(character) >= 0;
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
