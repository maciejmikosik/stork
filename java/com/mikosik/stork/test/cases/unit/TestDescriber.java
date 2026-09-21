package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.compile.Problem.problem;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Bracket.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.IntegerLiteral.literal;
import static com.mikosik.stork.model.token.Label.label;
import static com.mikosik.stork.model.token.StringLiteral.literal;
import static com.mikosik.stork.model.token.Symbol.DOT;
import static com.mikosik.stork.problem.Describer.describe;
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static com.mikosik.stork.test.Factories.identifier;
import static com.mikosik.stork.test.Factories.namespace;
import static java.math.BigInteger.valueOf;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.compile.Problem;

public class TestDescriber {
  public static Test testDescriber() {
    return suite("describes problem")
        .add(suite("with field")
            .add(test("string", "string"))
            .add(suite("expression")
                .add(test(variable("variable"), "variable"))
                .add(test(namespace("a/b/c"), "a/b/c"))
                .add(test(identifier("a/b/c/d"), "a/b/c/d")))
            .add(suite("character")
                .add(test(
                    (byte) 65,
                    outline("ascii")
                        .nest("printed: [A]")
                        .nest("decimal: 65")))
                .add(test(
                    (byte) 10,
                    outline("non-printable ascii")
                        .nest("decimal: 10")))
                .add(test(
                    (byte) 200,
                    outline("non-ascii")
                        .nest("decimal: 200"))))
            .add(suite("token")
                .add(test(label("label"), "label"))
                .add(test(LEFT_CURLY_BRACKET, "{"))
                .add(test(DOT, "."))
                .add(test(literal(valueOf(123)), "123"))
                .add(test(literal("stringLiteral"), "stringLiteral"))));
  }

  private static Test test(Object objectValue, String expected) {
    return test(
        objectValue.getClass().getSimpleName(),
        problem("mockName")
            .object(objectValue)
            .build(),
        outline("mockName")
            .nest("object: " + expected));
  }

  private static Test test(Object objectValue, Outline expected) {
    return test(
        objectValue.getClass().getSimpleName(),
        problem("mockName")
            .object(objectValue)
            .build(),
        outline("mockName")
            .nest(outline("object:")
                .nest(expected)));
  }

  private static Test test(String name, Problem problem, Outline outline) {
    return newCase(name, () -> {
      assertMatch(
          outline,
          describe(exception(problem)));
    });
  }
}
