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

public class TestDescriber {
  public static class ProblemWithExpressions {
    public String keyString = "valueString";
    public Variable keyVariable = variable("valueVariable");
    public Identifier keyIdentifier = identifier(
        namespace(list("a", "b")),
        variable("valueIdentifier"));
  }

  public static class ProblemWithBytes {
    public byte key65 = 65;
    public byte key10 = 10;
    public byte key200 = (byte) 200;
  }

  public static class ProblemWithTokens {
    public Label keyLabel = label("valueLabel");
    public Bracket keyBracket = LEFT_CURLY_BRACKET;
    public Symbol keySymbol = DOT;
    public IntegerLiteral keyIntegerLiteral = literal(valueOf(123));
    public StringLiteral keyStringLiteral = literal("valueStringLiteral");
  }

  public static Test testDescriber() {
    return suite("describer can describe problem with")
        .add(newCase("fields of type Expression", () -> {
          assertMatch(
              outline("ProblemWithExpressions")
                  .nest(outline("keyString: valueString"))
                  .nest(outline("keyVariable: valueVariable"))
                  .nest(outline("keyIdentifier: a/b/valueIdentifier")),
              describe(new ProblemWithExpressions()));
        }))
        .add(newCase("fields of type byte", () -> {
          assertMatch(
              outline("ProblemWithBytes")
                  .nest(outline("key65: ascii character [A]"))
                  .nest(outline("key10: non-printable ascii character with decimal value of 10"))
                  .nest(outline("key200: non-ascii character with decimal value of 200")),
              describe(new ProblemWithBytes()));
        }))
        .add(newCase("fields of type Token", () -> {
          assertMatch(
              outline("ProblemWithTokens")
                  .nest(outline("keyLabel: valueLabel"))
                  .nest(outline("keyBracket: {"))
                  .nest(outline("keySymbol: ."))
                  .nest(outline("keyIntegerLiteral: 123"))
                  .nest(outline("keyStringLiteral: valueStringLiteral")),
              describe(new ProblemWithTokens()));
        }));
  }
}
