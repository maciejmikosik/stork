package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Serializables.ascii;
import static com.mikosik.stork.compile.link.Bind.removeNamespaces;
import static com.mikosik.stork.compile.link.Combinator.B;
import static com.mikosik.stork.compile.link.Combinator.C;
import static com.mikosik.stork.compile.link.Combinator.I;
import static com.mikosik.stork.compile.link.Combinator.K;
import static com.mikosik.stork.compile.link.Combinator.S;
import static com.mikosik.stork.compile.link.MathOperator.ADD;
import static com.mikosik.stork.compile.link.MathOperator.COMPARE;
import static com.mikosik.stork.compile.link.MathOperator.DIVIDE;
import static com.mikosik.stork.compile.link.MathOperator.EQUAL;
import static com.mikosik.stork.compile.link.MathOperator.MULTIPLY;
import static com.mikosik.stork.compile.link.MathOperator.NEGATE;
import static com.mikosik.stork.compile.link.StackOperator.EAGER;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.CLOSE;
import static com.mikosik.stork.program.Stdout.writeByteTo;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.math.BigInteger;
import java.util.function.Function;

import org.quackery.Test;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.debug.Decompiler;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;
import com.mikosik.stork.model.Parameter;

public class TestDecompiler {
  public static Test testDecompiler() {
    Parameter x = parameter("x");
    Parameter y = parameter("y");
    return suite("decompiler decompiles")
        .add(suite("expression")
            .add(suite("integer")
                .add(test("0", integer(BigInteger.ZERO)))
                .add(test("123", integer(123)))
                .add(test("-123", integer(-123))))
            .add(suite("quote")
                .add(test("\"example quote\"", quote("example quote")))
                .add(test("\"\"", quote(""))))
            .add(suite("operators")
                .add(test("$OPERATOR", new Operator() {
                  public Computation compute(Stack stack) {
                    return null;
                  }

                  public String toString() {
                    return "OPERATOR";
                  }
                }))
                .add(suite("stack")
                    .add(test("$EAGER", EAGER)))
                .add(suite("math")
                    .add(test("$EQUAL", EQUAL))
                    .add(test("$COMPARE", COMPARE))
                    .add(test("$NEGATE", NEGATE))
                    .add(test("$ADD", ADD))
                    .add(test("$MULTIPLY", MULTIPLY))
                    .add(test("$DIVIDE", DIVIDE)))
                .add(suite("combinator")
                    .add(test("$I", I))
                    .add(test("$K", K))
                    .add(test("$S", S))
                    .add(test("$C", C))
                    .add(test("$B", B)))
                .add(suite("program")
                    .add(test("$WRITE", writeByteTo(null)))
                    .add(test("$CLOSE", CLOSE))
                    .add(test("$STDIN(7)", stdin(input(new byte[0]), 7)))
                    .add(test("$STDIN(0)", stdin(input(new byte[0]))))))
            .add(suite("variable")
                .add(test("var", variable("var"))))
            .add(suite("identifier")
                .add(test("iden", identifier("iden"))))
            .add(suite("parameter")
                .add(test("param", parameter("param"))))
            .add(suite("lambda")
                .add(test("(x){x}", lambda(x, x)))
                .add(test("(x)(y){x(y)}", lambda(x, y, application(x, y)))))
            .add(suite("application")
                .add(test("f(x)", application(identifier("f"), identifier("x"))))
                .add(test("f(x)(y)",
                    application(identifier("f"), identifier("x"), identifier("y"))))))
        .add(suite("definition")
            .add(test("f(x){x}", definition("f", lambda(x, x))))
            .add(test("f(x)(y){x}", definition("f", lambda(x, y, x))))
            .add(test("f{g}", definition("f", identifier("g")))))
        .add(suite("local")
            .add(test("function", removeNamespaces(identifier("package/package/function"))))
            .add(test("function", removeNamespaces(identifier("function")))));
  }

  private static Test test(String expected, Expression expression) {
    return test(expected, expression, Decompiler::decompile);
  }

  private static Test test(String expected, Definition definition) {
    return test(expected, definition, Decompiler::decompile);
  }

  private static <M> Test test(String expected, M model, Function<M, Serializable> decompiler) {
    return newCase(expected, () -> {
      String actual = ascii(decompiler.apply(model));
      if (!expected.equals(actual)) {
        throw assertException("""
            expected
              %s
            but was
              %s
            """,
            expected,
            actual);
      }
    });
  }
}
