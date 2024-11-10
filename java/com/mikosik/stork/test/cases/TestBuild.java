package com.mikosik.stork.test.cases;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.build.link.DuplicatedDefinition.duplicatedDefinition;
import static com.mikosik.stork.problem.build.link.UndefinedImport.undefinedImport;
import static com.mikosik.stork.problem.build.link.UndefinedVariable.undefinedVariable;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.illegalCode;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.illegalCodeInStringLiteral;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.range;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.Expression;

public class TestBuild {
  private static Expression body = identifier("body");

  public static Test testBuild() {
    return suite("build")
        .add(suite("reports")
            .add(suite("during compiling")
                .add(suite("illegal code in string literal")
                    .add(suite("ascii")
                        .add(reportsIllegalAsciiCodeInStringLiteral(0, 31))
                        .add(reportsIllegalAsciiCodeInStringLiteral(127)))
                    .add(reportsNonAsciiCodeInStringLiteral()))
                .add(suite("illegal code anywhere")
                    .add(reportsIllegalAsciiCodeAnywhere())
                    .add(reportsNonAsciiCodeAnywhere())))
            .add(suite("during linking")
                .add(reportsUndefinedVariable())
                .add(reportsUndefinedImport())
                .add(reportsDuplicatedDefinition())));
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
            .map(TestBuild::reportsIllegalAsciiCodeInStringLiteral)
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
        || "+-(){}\"\t\r\n ".indexOf(code) >= 0;
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

  private static Test reportsUndefinedImport() {
    return programTest("undefined import")
        .importFile("""
            namespace.function2
            """)
        .sourceFile("""
            function { function2 }
            """)
        .expect(undefinedImport(
            definition(identifier("function"), body),
            identifier("namespace.function2")));
  }

  private static Test reportsDuplicatedDefinition() {
    return suite("duplicated definition")
        .add(programTest("of user functions")
            .sourceFile("""
                function { 1 }
                function { 2 }
                """)
            .expect(duplicatedDefinition(identifier("function"))))
        .add(programTest("with core function")
            .file("lang/stream/source", """
                length { 1 }
                """)
            .expect(duplicatedDefinition(identifier("lang.stream.length"))));
  }
}
