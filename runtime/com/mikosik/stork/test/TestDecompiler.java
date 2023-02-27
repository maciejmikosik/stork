package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.NamedInstruction.name;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.CLOSE_STREAM;
import static com.mikosik.stork.program.Stdout.writeByte;
import static com.mikosik.stork.tool.common.Combinator.B;
import static com.mikosik.stork.tool.common.Combinator.C;
import static com.mikosik.stork.tool.common.Combinator.I;
import static com.mikosik.stork.tool.common.Combinator.K;
import static com.mikosik.stork.tool.common.Combinator.S;
import static com.mikosik.stork.tool.common.Combinator.Y;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static com.mikosik.stork.tool.link.MathModule.mathModule;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Optional;

import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.EagerInstruction;
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
            .add(testInstructions())
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
        .add(suite("module")
            .add(test("", module(Chain.empty())))
            .add(test("f{x}", module(chainOf(
                definition("f", identifier("x"))))))
            .add(test("f{x} g{y}", module(chainOf(
                definition("f", identifier("x")),
                definition("g", identifier("y")))))))
        .add(suite("local")
            .add(test(decompiler().local(), "function", identifier("package.package.function")))
            .add(test(decompiler().local(), "function", identifier("function"))));
  }

  private static Suite testInstructions() {
    Instruction instruction = x -> (Instruction) y -> identifier("z");
    Instruction f = name("f", instruction);

    Identifier x = identifier("x");
    Identifier y = identifier("y");

    return suite("instruction")
        .add(suite("raw")
            .add(test("<>", instruction))
            .add(test("<>", instruction.apply(x)))
            .add(test("<>(x)", application(instruction, x)))
            .add(test("z", apply(instruction, x, y))))
        .add(suite("wrapped")
            .add(test("<f>", f))
            .add(test("<f(x)>", apply(f, x)))
            .add(test("<f(x)>(y)", application(apply(f, x), y)))
            .add(test("z", apply(f, x, y)))
            .add(test("eager(<>)", eager(instruction)))
            .add(test("eager(<f>)", eager(f)))
            .add(test("eager(<f(x)>)", apply(eager(f), x)))
            .add(test("eagerVisited(<f>)", eager(f).visit())))
        .add(suite("nested")
            .add(test("<f>", nest(f)))
            .add(test("<f(x)>", apply(nest(f), x)))
            .add(test("<f(x)>(y)", application(apply(nest(f), x), y)))
            .add(test("z", apply(nest(f), x, y))))
        .add(suite("combinators")
            .add(test("<S>", S))
            .add(test("<S(x)>", apply(S, x)))
            .add(test("<S(x)(y)>", apply(S, x, y)))
            .add(test("<K>", K))
            .add(test("<K(x)>", apply(K, x)))
            .add(test("<I>", I))
            .add(test("<C>", C))
            .add(test("<C(x)>", apply(C, x)))
            .add(test("<C(x)(y)>", apply(C, x, y)))
            .add(test("<B>", B))
            .add(test("<B(x)>", apply(B, x)))
            .add(test("<B(x)(y)>", apply(B, x, y)))
            .add(test("<Y>", Y))
            .add(test("x(<Y>(x))", apply(Y, x))))
        .add(suite("math")
            .add(test("<stork.integer.native.NEGATE>", math("NEGATE")))
            .add(test("<stork.integer.native.ADD>", math("ADD")))
            .add(test("<stork.integer.native.EQUAL>", math("EQUAL")))
            .add(test("<stork.integer.native.MORETHAN>", math("MORETHAN"))))
        .add(suite("stdout")
            .add(test("eager(<writeByte>)", writeByte(null)))
            .add(test("<closeStream>", CLOSE_STREAM)));
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

  private static Instruction nest(Instruction instruction) {
    return argument -> {
      Expression applied = instruction.apply(argument);
      return applied instanceof Instruction appliedInstruction
          ? nest(appliedInstruction)
          : applied;
    };
  }

  private static Expression math(String name) {
    Optional<Expression> maybeFound = mathModule().definitions.stream()
        .filter(definition -> definition.identifier.name.endsWith(name))
        .map(definition -> definition.body)
        .findFirst();
    if (maybeFound.isPresent()) {
      EagerInstruction eager = (EagerInstruction) maybeFound.get();
      return eager.instruction;
    } else {
      return identifier("NOT FOUND");
    }
  }
}
