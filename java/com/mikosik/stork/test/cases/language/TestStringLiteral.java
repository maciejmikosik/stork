package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInString.illegalCharacterInString;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestStringLiteral {
  public static Test testStringLiteral() {
    return suite("string literal")
        .add(suite("cannot have illegal characters")
            .addAll(concat(rangeClosed(0, 31), rangeClosed(127, 255))
                .mapToObj(value -> Byte.valueOf((byte) value))
                .map(TestStringLiteral::reportsIllegalCharacter)
                .toList()));
  }

  private static Test reportsIllegalCharacter(byte character) {
    return programTest("[%d]".formatted(character))
        .sourceRaw(stringLiteralWith(character))
        .expect(illegalCharacterInString(character));
  }

  private static byte[] stringLiteralWith(byte character) {
    var bytes = "main(stdin) { \"?\" }".getBytes(UTF_8);
    byte questionMark = '?';
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] == questionMark) {
        bytes[i] = character;
      }
    }
    return bytes;
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
