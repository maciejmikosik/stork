package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode.illegalCharacterInCode;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestTokenizer {
  public static Test testTokenizer() {
    return suite("tokenizer")
        .add(suite("reports illegal character in source code")
            .addAll(rangeClosed(0, 255)
                .mapToObj(value -> Byte.valueOf((byte) value))
                .filter(character -> !isLegal(character))
                .map(TestTokenizer::reportsIllegalCharacter)
                .toList()));
  }

  private static Test reportsIllegalCharacter(byte character) {
    return programTest("[%d]".formatted(character))
        .sourceRaw(sourceCodeWith(character))
        .expect(illegalCharacterInCode(character));
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

  private static boolean isLegal(int code) {
    return 'A' <= code && code <= 'Z'
        || 'a' <= code && code <= 'z'
        || '0' <= code && code <= '9'
        || "+-(){}.\"\t\r\n ".indexOf(code) >= 0;
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
