package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.compile.err.Problems.illegalCharacterInCode;
import static com.mikosik.stork.compile.err.Problems.malformedImportLine;
import static com.mikosik.stork.compile.err.Problems.unexpectedToken;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.test.Factories.namespace;
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
        .add(path()
            .imports("x xx xxx")
            .source("main(stdin) { ! }"))
        .add(path("a/aa")
            .imports("y yy yyy")
            .source("func ."))
        .expect(
            malformedImportLine()
                .in(root)
                .object("x xx xxx"),
            illegalCharacterInCode()
                .in(root)
                .in(variable("main"))
                .object((byte) '!'),
            malformedImportLine()
                .in(namespace("a/aa"))
                .object("y yy yyy"),
            unexpectedToken()
                .in(namespace("a/aa"))
                .in(variable("func"))
                .object(DOT));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
