package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.noOutput;
import static com.mikosik.stork.common.io.Serializables.ascii;
import static com.mikosik.stork.compile.link.Bind.removeNamespaces;
import static com.mikosik.stork.compile.link.CombinatoryModule.B;
import static com.mikosik.stork.compile.link.CombinatoryModule.C;
import static com.mikosik.stork.compile.link.CombinatoryModule.I;
import static com.mikosik.stork.compile.link.CombinatoryModule.K;
import static com.mikosik.stork.compile.link.CombinatoryModule.S;
import static com.mikosik.stork.compile.link.CombinatoryModule.Y;
import static com.mikosik.stork.compile.link.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.link.MathModule.ADD;
import static com.mikosik.stork.compile.link.MathModule.DIVIDE_BY;
import static com.mikosik.stork.compile.link.MathModule.EQUAL;
import static com.mikosik.stork.compile.link.MathModule.MORE_THAN;
import static com.mikosik.stork.compile.link.MathModule.MULTIPLY;
import static com.mikosik.stork.compile.link.MathModule.NEGATE;
import static com.mikosik.stork.compile.link.MathModule.mathModule;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.debug.InjectNames.injectNames;
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
import static com.mikosik.stork.program.ProgramModule.CLOSE_STREAM;
import static com.mikosik.stork.program.ProgramModule.WRITE_BYTE;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static com.mikosik.stork.program.Stdin.stdin;
import static com.mikosik.stork.program.Stdout.stdout;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.math.BigInteger;
import java.util.function.Function;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.common.io.Serializable;
import com.mikosik.stork.debug.Decompiler;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;
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
            .add(suite("stdout")
                .add(test("stdout", stdout(noOutput()))))
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
                .add(test("<g>", apply(name("f", a -> a), name("g", b -> b))))))
        .add(suite("combinators")
            .add(test("<lang.native.combinator.s>", inst(S)))
            .add(test("<lang.native.combinator.s(x)>", apply(inst(S), x)))
            .add(test("<lang.native.combinator.s(x)(y)>", apply(inst(S), x, y)))
            .add(test("<lang.native.combinator.k>", inst(K)))
            .add(test("<lang.native.combinator.k(x)>", apply(inst(K), x)))
            .add(test("<lang.native.combinator.i>", inst(I)))
            .add(test("<lang.native.combinator.c>", inst(C)))
            .add(test("<lang.native.combinator.c(x)>", apply(inst(C), x)))
            .add(test("<lang.native.combinator.c(x)(y)>", apply(inst(C), x, y)))
            .add(test("<lang.native.combinator.b>", inst(B)))
            .add(test("<lang.native.combinator.b(x)>", apply(inst(B), x)))
            .add(test("<lang.native.combinator.b(x)(y)>", apply(inst(B), x, y)))
            .add(test("<lang.native.combinator.y>", inst(Y)))
            .add(test("x(lang.native.combinator.y(x))", apply(inst(Y), x))))
        .add(suite("math")
            .add(test("<lang.native.integer.equal>", inst(EQUAL)))
            .add(test("<lang.native.integer.moreThan>", inst(MORE_THAN)))
            .add(test("<lang.native.integer.negate>", inst(NEGATE)))
            .add(test("<lang.native.integer.add>", inst(ADD)))
            .add(test("<lang.native.integer.multiply>", inst(MULTIPLY)))
            .add(test("<lang.native.integer.divideBy>", inst(DIVIDE_BY))))
        .add(suite("program")
            .add(test("<lang.native.program.writeByte>", inst(WRITE_BYTE)))
            .add(test("<lang.native.program.closeStream>", inst(CLOSE_STREAM))));
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

  private static Instruction inst(Identifier identifier) {
    return instructionsModule.definitions.stream()
        .filter(definition -> definition.identifier.equals(identifier))
        .map(definition -> definition.body)
        .map(body -> body instanceof EagerInstruction eager
            ? eager.instruction
            : body)
        .map(body -> (Instruction) body)
        .findFirst()
        .orElseThrow();
  }

  private static final Module instructionsModule = injectNames(join(
      mathModule(),
      combinatoryModule(),
      programModule()));
}
