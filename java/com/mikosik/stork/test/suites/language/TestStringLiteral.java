package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.compile.err.Problems.illegalCharacterInStringLiteral;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.build.SourceBuilder.mainWithBody;
import static com.mikosik.stork.test.build.SourceBuilder.stringLiteral;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.rangeClosed;
import static org.quackery.Suite.suite;

import java.util.LinkedList;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestStringLiteral {
  public static Test testStringLiteral() {
    return suite("string literal")
        .add(allowsLegalCharacters())
        .add(reportsIllegalCharacters());
  }

  private static Test allowsLegalCharacters() {
    var legalCharacterTests = new LinkedList<Test>();
    var legalStringBuilder = new StringBuilder();

    for (byte character = 32; character <= 126; character++) {
      if (character != '"') {
        legalCharacterTests.add(programTest(Character.toString(character))
            .source(mainWithBody(stringLiteral(character)))
            .stdout(Character.toString(character)));
        legalStringBuilder.append((char) character);
      }
    }
    var legalString = legalStringBuilder.toString();

    return suite("allows legal characters")
        .add(suite("single character")
            .addAll(legalCharacterTests))
        .add(programTest("all in one string")
            .source(mainWithBody(stringLiteral(legalString)))
            .stdout(legalString));
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
        .source(mainWithBody(stringLiteral(character)))
        .expect(illegalCharacterInStringLiteral()
            .in(root)
            .in(variable("main"))
            .object(character));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
