package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespace;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Bracket.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.IntegerLiteral.literal;
import static com.mikosik.stork.model.token.Label.label;
import static com.mikosik.stork.model.token.StringLiteral.literal;
import static com.mikosik.stork.model.token.Symbol.DOT;
import static com.mikosik.stork.problem.Describer.describe;
import static com.mikosik.stork.problem.compile.CompilerException.exception;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static java.math.BigInteger.valueOf;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.model.token.Bracket;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Symbol;
import com.mikosik.stork.problem.compile.CannotCompile;

public class TestDescriber {
  public static class ProblemWithExpressions extends CannotCompile {
    public String keyString = "valueString";
    public Variable keyVariable = variable("valueVariable");
    public Identifier keyIdentifier = identifier(
        namespace(list("a", "b")),
        variable("valueIdentifier"));
  }

  public static class ProblemWithBytes extends CannotCompile {
    public byte key65 = 65;
    public byte key10 = 10;
    public byte key200 = (byte) 200;
  }

  public static class ProblemWithTokens extends CannotCompile {
    public Label keyLabel = label("valueLabel");
    public Bracket keyBracket = LEFT_CURLY_BRACKET;
    public Symbol keySymbol = DOT;
    public IntegerLiteral keyIntegerLiteral = literal(valueOf(123));
    public StringLiteral keyStringLiteral = literal("valueStringLiteral");
  }

  public static class Problem extends CannotCompile {
    public int value;

    private Problem(int value) {
      this.value = value;
    }
  }

  public static Test testDescriber() {
    return suite("describer can describe problem with")
        .add(newCase("fields of type Expression", () -> {
          assertMatch(
              outline("ProblemWithExpressions")
                  .nest("keyString: valueString")
                  .nest("keyVariable: valueVariable")
                  .nest("keyIdentifier: a/b/valueIdentifier"),
              describe(exception(new ProblemWithExpressions())));
        }))
        .add(newCase("fields of type byte", () -> {
          assertMatch(
              outline("ProblemWithBytes")
                  .nest(outline("key65:")
                      .nest(outline("ascii")
                          .nest("printed: [A]")
                          .nest("decimal: 65")))
                  .nest(outline("key10:")
                      .nest(outline("non-printable ascii")
                          .nest("decimal: 10")))
                  .nest(outline("key200:")
                      .nest(outline("non-ascii")
                          .nest("decimal: 200"))),
              describe(exception(new ProblemWithBytes())));
        }))
        .add(newCase("fields of type Token", () -> {
          assertMatch(
              outline("ProblemWithTokens")
                  .nest("keyLabel: valueLabel")
                  .nest("keyBracket: {")
                  .nest("keySymbol: .")
                  .nest("keyIntegerLiteral: 123")
                  .nest("keyStringLiteral: valueStringLiteral"),
              describe(exception(new ProblemWithTokens())));
        }))
        .add(newCase("multiple problems", () -> {
          assertMatch(
              outline("cannot compile")
                  .nest(describe(exception(new Problem(1))))
                  .nest(describe(exception(new Problem(2))))
                  .nest(describe(exception(new Problem(3)))),
              describe(exception(list(
                  new Problem(1),
                  new Problem(2),
                  new Problem(3)))));
        }));
  }
}
