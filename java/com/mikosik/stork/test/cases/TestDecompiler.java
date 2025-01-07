package com.mikosik.stork.test.cases;

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
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.moduleOf;
import static com.mikosik.stork.model.NamedInstruction.name;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.program.Program.CLOSE_STREAM;
import static com.mikosik.stork.program.Program.writeByteTo;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.debug.Decompiler;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;
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
            .add(suite("stdin")
                .add(test("stdin(7)", stdin(input(new byte[0]), 7)))
                .add(test("stdin(0)", stdin(input(new byte[0])))))
            .add(suite("operators")
                .add(test("$OPERATOR", new Operator() {
                  public Optional<Computation> compute(Stack stack) {
                    return Optional.empty();
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
                    .add(test("$CLOSE", CLOSE_STREAM))))
            .add(testInstructions())
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
        .add(suite("module")
            .add(test("", moduleOf()))
            .add(test("f{x}", moduleOf(
                definition("f", identifier("x")))))
            .add(test("f{x} g{y}", moduleOf(
                definition("f", identifier("x")),
                definition("g", identifier("y"))))))
        .add(suite("local")
            .add(test("function", removeNamespaces(identifier("package.package.function"))))
            .add(test("function", removeNamespaces(identifier("function")))));
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
        .add(suite("nested")
            .add(test("<f>", f))
            .add(test("<f(x)>", apply(f, x)))
            .add(test("<f(x)>(y)", application(apply(f, x), y)))
            .add(test("z", apply(f, x, y)))
            .add(test("eager(<>)", eager(instruction)))
            .add(test("eager(<f>)", eager(f)))
            .add(test("eager(<f(x)>)", apply(eager(f), x)))
            .add(test("eagerVisited(<f>)", eager(f).visit()))
            .add(suite("detects returning other named instruction")
                .add(test("<g>", apply(name("f", a -> name("g", b -> b)), x)))
                .add(test("<g>", apply(name("f", a -> a), name("g", b -> b))))));
  }

  private static Test test(String expected, Expression expression) {
    return test(expected, expression, Decompiler::decompile);
  }

  private static Test test(String expected, Definition definition) {
    return test(expected, definition, Decompiler::decompile);
  }

  private static Test test(String expected, Module module) {
    return test(expected, module, Decompiler::decompile);
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

  private static Expression apply(Instruction instruction, Expression... arguments) {
    Expression result = instruction;
    for (Expression argument : arguments) {
      result = ((Instruction) result).apply(argument);
    }
    return result;
  }
}
