package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Eager.eager;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.math.BigInteger;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Combinator;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Stack;
import com.mikosik.stork.tool.decompile.Decompiler;

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
            .add(suite("innate")
                .add(test("innate_name", mockInnate("innate_name")))
                .add(suite("eager")
                    .add(test("$EAGER_1(function)", eager(1, variable("function"))))
                    .add(test("$EAGER_1($EAGER_2(function))", eager(2, variable("function")))))
                .add(suite("combinator")
                    .add(test("$I", Combinator.I))
                    .add(test("$K", Combinator.K))
                    .add(test("$S", Combinator.S))
                    .add(test("$C", Combinator.C))
                    .add(test("$B", Combinator.B))
                    .add(test("$Y", Combinator.Y))))
            .add(suite("variable")
                .add(test("var", variable("var"))))
            .add(suite("parameter")
                .add(test("param", parameter("param"))))
            .add(suite("lambda")
                .add(test("(x){x}", lambda(x, x)))
                .add(test("(x)(y){x(y)}", lambda(x, lambda(y, application(x, y))))))
            .add(suite("application")
                .add(test("f(x)", application(variable("f"), variable("x"))))
                .add(test("f(x)(y)", application(variable("f"), variable("x"), variable("y"))))))
        .add(suite("definition")
            .add(test("f(x){x}", definition(identifier("f"), lambda(x, x))))
            .add(test("f(x)(y){x}", definition(identifier("f"), lambda(x, lambda(y, x)))))
            .add(test("f{g}", definition(identifier("f"), variable("g")))))
        .add(suite("module")
            .add(test("", module(Chain.empty())))
            .add(test("f{x}", module(chainOf(
                definition(identifier("f"), variable("x"))))))
            .add(test("f{x} g{y}", module(chainOf(
                definition(identifier("f"), variable("x")),
                definition(identifier("g"), variable("y")))))))
        .add(suite("local")
            .add(test(decompiler().local(), "function", identifier("package.package.function")))
            .add(test(decompiler().local(), "function", identifier("function"))));
  }

  private static Test test(String expected, Model code) {
    return test(decompiler(), expected, code);
  }

  private static Test test(Decompiler decompiler, String expected, Model code) {
    return newCase(expected, () -> {
      run(decompiler, code, expected);
    });
  }

  private static void run(Decompiler decompiler, Model code, String expected) {
    String actual = decompiler.decompile(code);
    if (!expected.equals(actual)) {
      throw new AssertException(format(""
          + "expected\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          expected,
          actual));
    }
  }

  private static Innate mockInnate(String name) {
    return new Innate() {
      public Computation compute(Stack stack) {
        return null;
      }

      public String toString() {
        return name;
      }
    };
  }
}
