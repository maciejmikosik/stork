package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacter.illegalCharacter;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacter.illegalStringCharacter;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestTokenizerProblems {
  public static Test testTokenizerProblems() {
    return suite("tokenizer reports")
        .add(suite("illegal code in string literal")
            .add(suite("ascii")
                .add(reportsIllegalAsciiCodeInStringLiteral(0, 31))
                .add(reportsIllegalAsciiCodeInStringLiteral(127)))
            .add(reportsNonAsciiCodeInStringLiteral()))
        .add(suite("illegal code anywhere")
            .add(reportsIllegalAsciiCodeAnywhere())
            .add(reportsNonAsciiCodeAnywhere()));
  }

  /*
   * TODO Make this test more generic by allowing non-ascii codes. This requires
   * .sourceFile function to accept byte[].
   */
  private static Test reportsIllegalAsciiCodeInStringLiteral(int code) {
    return programTest("code: %d".formatted(code))
        .sourceRaw("""
            function { "%c" }
            """.formatted(code))
        .expect(illegalStringCharacter((byte) code));
  }

  private static Test reportsIllegalAsciiCodeInStringLiteral(int firstCode, int lastCode) {
    return suite("range <%d, %d>".formatted(firstCode, lastCode))
        .addAll(range(firstCode, lastCode + 1)
            .mapToObj(value -> Byte.valueOf((byte) value))
            .map(TestTokenizerProblems::reportsIllegalAsciiCodeInStringLiteral)
            .toList());
  }

  private static Test reportsNonAsciiCodeInStringLiteral() {
    return programTest("non-ascii")
        .sourceRaw("""
            function { "\u00FC" }
            """)
        .expect(illegalStringCharacter("\u00FC".getBytes(UTF_8)[0]));
  }

  private static Test reportsIllegalAsciiCodeAnywhere() {
    return suite("ascii")
        .addAll(range(0, 128)
            .filter(code -> !isLegal(code))
            .mapToObj(code -> programTest("code: %d".formatted(code))
                .sourceRaw("""
                    function%c { 0 }
                    """.formatted(code))
                .expect(illegalCharacter((byte) code)))
            .toList());
  }

  private static boolean isLegal(int code) {
    return 'A' <= code && code <= 'Z'
        || 'a' <= code && code <= 'z'
        || '0' <= code && code <= '9'
        || "+-(){}.\"\t\r\n ".indexOf(code) >= 0;
  }

  private static Test reportsNonAsciiCodeAnywhere() {
    return programTest("non-ascii")
        .sourceRaw("""
            function\u00FC { 0 }
            """)
        .expect(illegalCharacter("\u00FC".getBytes(UTF_8)[0]));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
