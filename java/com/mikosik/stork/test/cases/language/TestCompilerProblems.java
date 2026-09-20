package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static com.mikosik.stork.model.token.Symbol.DOT;
import static com.mikosik.stork.problem.compile.importing.MalformedImportLine.malformedImportLine;
import static com.mikosik.stork.problem.compile.parse.UnexpectedToken.unexpected;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode.illegalCharacterInCode;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestCompilerProblems {
  public static Test testCompilerProblems() {
    return suite("compiler")
        .add(aggregatesAllProblems());
  }

  private static Test aggregatesAllProblems() {
    return programTest("aggregates all problems")
        .imports("x xx xxx")
        .source("main(stdin) { ! }")
        .add(path("x").source("func ."))
        .expect(
            malformedImportLine(namespaceRoot(), "x xx xxx"),
            illegalCharacterInCode((byte) '!'),
            unexpected(DOT));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
