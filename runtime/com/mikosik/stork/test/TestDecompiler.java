package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Eager.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.tool.common.CombinatorModule.combinatorModule;
import static com.mikosik.stork.tool.common.Constants.B;
import static com.mikosik.stork.tool.common.Constants.C;
import static com.mikosik.stork.tool.common.Constants.I;
import static com.mikosik.stork.tool.common.Constants.K;
import static com.mikosik.stork.tool.common.Constants.S;
import static com.mikosik.stork.tool.common.Constants.Y;
import static com.mikosik.stork.tool.common.Instructions.name;
import static com.mikosik.stork.tool.common.MathModule.mathModule;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Eager;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Model;
import com.mikosik.stork.model.Parameter;
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
            .add(suite("stdin")
                .add(test("stdin(7)", stdin(mockInput(), 7)))
                .add(test("stdin(0)", stdin(mockInput()))))
            .add(test("eager(function)", eager(identifier("function"))))
            .add(testInstructions())
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

  private static Suite testInstructions() {
    Instruction instruction = x -> (Instruction) y -> identifier("z");
    Instruction f = name(identifier("f"), instruction);

    Identifier x = identifier("x");
    Identifier y = identifier("y");

    return suite("instruction")
        .add(suite("raw")
            .add(test("<>", instruction))
            .add(test("<>", instruction.apply(x)))
            .add(test("<>(x)", application(instruction, x)))
            .add(test("z", apply(instruction, x, y))))
        .add(suite("named")
            .add(test("<f>", f))
            .add(test("<f(x)>", apply(f, x)))
            .add(test("<f(x)>(y)", application(apply(f, x), y)))
            .add(test("z", apply(f, x, y))))
        .add(suite("nested")
            .add(test("<f>", nest(f)))
            .add(test("<f(x)>", apply(nest(f), x)))
            .add(test("<f(x)>(y)", application(apply(nest(f), x), y)))
            .add(test("z", apply(nest(f), x, y))))
        .add(suite("combinators")
            .add(test("<stork.inst.S>", combinator(S)))
            .add(test("<stork.inst.S(x)>", apply(combinator(S), x)))
            .add(test("<stork.inst.S(x)(y)>", apply(combinator(S), x, y)))
            .add(test("<stork.inst.K>", combinator(K)))
            .add(test("<stork.inst.K(x)>", apply(combinator(K), x)))
            .add(test("<stork.inst.I>", combinator(I)))
            .add(test("<stork.inst.C>", combinator(C)))
            .add(test("<stork.inst.C(x)>", apply(combinator(C), x)))
            .add(test("<stork.inst.C(x)(y)>", apply(combinator(C), x, y)))
            .add(test("<stork.inst.B>", combinator(B)))
            .add(test("<stork.inst.B(x)>", apply(combinator(B), x)))
            .add(test("<stork.inst.B(x)(y)>", apply(combinator(B), x, y)))
            .add(test("<stork.inst.Y>", combinator(Y))))
        .add(suite("math")
            .add(test("<stork.integer.negate>", math("negate")))
            .add(test("<stork.integer.add>", math("add")))
            .add(test("<stork.integer.equal>", math("equal")))
            .add(test("<stork.integer.moreThan>", math("moreThan"))));
  }

  private static Test test(String expected, Model model) {
    return test(decompiler(), expected, model);
  }

  private static Test test(Decompiler decompiler, String expected, Model model) {
    return newCase(expected, () -> {
      run(decompiler, model, expected);
    });
  }

  private static void run(Decompiler decompiler, Model model, String expected) {
    String actual = decompiler.decompile(model);
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

  private static Input mockInput() {
    return input(new ByteArrayInputStream(new byte[0]));
  }

  private static Expression apply(Instruction instruction, Expression... arguments) {
    Expression result = instruction;
    for (Expression argument : arguments) {
      result = ((Instruction) result).apply(argument);
    }
    return result;
  }

  private static Instruction combinator(Identifier identifier) {
    return (Instruction) combinatorModule().definitions.stream()
        .filter(definition -> definition.identifier.name.equals(identifier.name))
        .map(definition -> definition.body)
        .findFirst()
        .get();
  }

  private static Instruction nest(Instruction instruction) {
    return argument -> {
      Expression applied = instruction.apply(argument);
      return applied instanceof Instruction
          ? nest((Instruction) applied)
          : applied;
    };
  }

  private static Expression math(String name) {
    Eager eager = (Eager) mathModule().definitions.stream()
        .filter(definition -> definition.identifier.name.endsWith(name))
        .map(definition -> definition.body)
        .findFirst()
        .get();
    return eager.function;
  }
}
