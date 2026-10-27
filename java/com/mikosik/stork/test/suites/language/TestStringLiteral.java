package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.common.io.BytesBuilder.bytesBuilder;
import static com.mikosik.stork.compile.err.Problems.illegalCharacterInStringLiteral;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestStringLiteral {
  public static Test testStringLiteral() {
    return suite("string literal")
        .add(allowsLegalCharacters())
        .add(reportsIllegalCharacters());
  }

  private static Test allowsLegalCharacters() {
    var legalCharacters = legalCharacters();
    return suite("allows legal characters")
        .addAll(legalCharacters.chars()
            .mapToObj(character -> (byte) character)
            .map(character -> programTest("%c".formatted(character))
                .source(mainWithStringLiteralWith(character))
                .stdout("%c".formatted(character)))
            .toList())
        .add(programTest("all in one string")
            .source("""
                main(stdin) {
                  "%s"
                }
                """
                .formatted(legalCharacters))
            .stdout(legalCharacters));
  }

  private static String legalCharacters() {
    var builder = new StringBuilder();
    rangeClosed(32, 126)
        .filter(character -> character != '\"')
        .forEach(character -> builder.append((char) character));
    return builder.toString();
  }

  private static Test reportsIllegalCharacters() {
    return suite("reports illegal characters")
        .addAll(concat(rangeClosed(0, 31), rangeClosed(127, 255))
            .mapToObj(value -> Byte.valueOf((byte) value))
            .map(TestStringLiteral::reportsIllegal)
            .toList());
  }

  private static Test reportsIllegal(byte character) {
    return programTest("[%d]".formatted(character))
        .source(mainWithStringLiteralWith(character))
        .expect(illegalCharacterInStringLiteral()
            .in(root)
            .in(variable("main"))
            .object(character));
  }

  private static byte[] mainWithStringLiteralWith(byte character) {
    return bytesBuilder()
        .appendAscii("main(stdin) { \"")
        .append(character)
        .appendAscii("\" }")
        .build();
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
