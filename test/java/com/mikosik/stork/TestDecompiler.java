package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Opcode.ADD;
import static com.mikosik.stork.data.model.Opcode.ARG_1;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Argument.argument;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Scope.GLOBAL;
import static com.mikosik.stork.tool.common.Scope.LOCAL;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.math.BigInteger;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Alien;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;
import com.mikosik.stork.data.model.comp.Stack;
import com.mikosik.stork.tool.decompile.Decompiler;

public class TestDecompiler {
  public static Test testDecompiler() {
    Parameter x = parameter("x");
    Parameter y = parameter("y");
    return suite("decompiler decompiles")
        .add(suite("expression")
            .add(suite("integer")
                .add(test("0", integer(BigInteger.ZERO)))
                .add(test("123", integer(BigInteger.valueOf(123))))
                .add(test("-123", integer(BigInteger.valueOf(-123)))))
            .add(suite("opcode")
                .add(test("ADD", ADD))
                .add(test("ARG_1", ARG_1)))
            .add(suite("alien")
                .add(test("alien_name", mockAlien("alien_name"))))
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
            .add(test("f(x){x}", definition(variable("f"), lambda(x, x))))
            .add(test("f(x)(y){x}", definition(variable("f"), lambda(x, lambda(y, x)))))
            .add(test("f{g}", definition(variable("f"), variable("g")))))
        .add(suite("module")
            .add(test("", module(Chain.empty())))
            .add(test("f{x}", module(chainOf(
                definition(variable("f"), variable("x"))))))
            .add(test("f{x} g{y}", module(chainOf(
                definition(variable("f"), variable("x")),
                definition(variable("g"), variable("y")))))))
        .add(suite("computation")
            .add(test("@(f)", computation(
                variable("f"),
                Empty.empty())))
            .add(test("f(@(g(y)))(y)", computation(
                application(variable("g"), variable("y")),
                function(variable("f"),
                    argument(variable("y"),
                        Empty.empty()))))))
        .add(suite("local")
            .add(testLocal("true", variable("stork.boolean.true"))));
  }

  private static Test test(String expected, Object code) {
    return newCase(expected, () -> run(decompiler(GLOBAL), code, expected));
  }

  private static Test testLocal(String expected, Object code) {
    return newCase(expected, () -> run(decompiler(LOCAL), code, expected));
  }

  private static void run(Decompiler decompiler, Object code, String expected) {
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

  private static Alien mockAlien(String name) {
    return new Alien() {
      public Computation compute(Stack stack) {
        return null;
      }

      public String toString() {
        return name;
      }
    };
  }
}
