package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.link.DuplicatedDefinition.duplicatedDefinition;
import static com.mikosik.stork.problem.compile.link.FunctionNotDefined.functionNotDefined;
import static com.mikosik.stork.problem.compile.link.UndefinedVariable.undefinedVariable;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCode.illegalCode;
import static com.mikosik.stork.problem.compile.tokenize.IllegalCode.illegalCodeInStringLiteral;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.test.ProgramTest;

public class TestCompilerProblems {
  private final static Expression body = identifier("body");

  public static Test testCompilerProblems() {
    return suite("compiler reports problems when")
        .add(suite("tokenizing")
            .add(suite("illegal code in string literal")
                .add(suite("ascii")
                    .add(reportsIllegalAsciiCodeInStringLiteral(0, 31))
                    .add(reportsIllegalAsciiCodeInStringLiteral(127)))
                .add(reportsNonAsciiCodeInStringLiteral()))
            .add(suite("illegal code anywhere")
                .add(reportsIllegalAsciiCodeAnywhere())
                .add(reportsNonAsciiCodeAnywhere())))
        .add(suite("linking")
            .add(reportsUndefinedVariable())
            .add(reportsFunctionNotDefined())
            .add(reportsDuplicatedDefinition()));
  }

  private static Test reportsIllegalAsciiCodeInStringLiteral(int code) {
    return programTest("code: %d".formatted(code))
        .sourceFile("""
            function { "%c" }
            """.formatted(code))
        .expect(illegalCodeInStringLiteral((byte) code));
  }

  private static Test reportsIllegalAsciiCodeInStringLiteral(int firstCode, int lastCode) {
    return suite("range <%d, %d>".formatted(firstCode, lastCode))
        .addAll(range(firstCode, lastCode + 1)
            .mapToObj(value -> Byte.valueOf((byte) value))
            .map(TestCompilerProblems::reportsIllegalAsciiCodeInStringLiteral)
            .toList());
  }

  private static Test reportsNonAsciiCodeInStringLiteral() {
    return programTest("non-ascii")
        .sourceFile("""
            function { "\u00FC" }
            """)
        .expect(illegalCodeInStringLiteral("\u00FC".getBytes(UTF_8)[0]));
  }

  private static Test reportsIllegalAsciiCodeAnywhere() {
    return suite("ascii")
        .addAll(range(0, 128)
            .filter(code -> !isLegal(code))
            .mapToObj(code -> programTest("code: %d".formatted(code))
                .sourceFile("""
                    function%c { 0 }
                    """.formatted(code))
                .expect(illegalCode((byte) code)))
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
        .sourceFile("""
            function\u00FC { 0 }
            """)
        .expect(illegalCode("\u00FC".getBytes(UTF_8)[0]));
  }

  private static Test reportsUndefinedVariable() {
    return programTest("undefined variable")
        .sourceFile("""
            function { variable }
            """)
        .expect(undefinedVariable(
            definition(identifier("function"), body),
            variable("variable")));
  }

  private static Test reportsFunctionNotDefined() {
    return programTest("function that is not defined")
        .importFile("""
            namespace.function2
            """)
        .sourceFile("""
            function { function2 }
            """)
        .expect(functionNotDefined(
            identifier("function"),
            identifier("namespace.function2")));
  }

  private static Test reportsDuplicatedDefinition() {
    return programTest("duplicated definition of user functions")
        .sourceFile("""
            function { 1 }
            function { 2 }
            """)
        .expect(duplicatedDefinition(identifier("function")));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
