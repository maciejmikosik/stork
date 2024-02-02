package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Logic.flip;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.compile.Bridge.instruction;
import static com.mikosik.stork.compile.Bridge.javaInteger;
import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;

public class MathModule {
  public static final Namespace NAMESPACE = namespace(sequence("lang", "native", "integer"));

  public static final Identifier EQUAL = id("equal");
  public static final Identifier MORE_THAN = id("moreThan");
  public static final Identifier NEGATE = id("negate");
  public static final Identifier ADD = id("add");
  public static final Identifier MULTIPLY = id("multiply");
  public static final Identifier DIVIDE_BY = id("divideBy");

  private static Identifier id(String name) {
    return identifier(NAMESPACE, variable(name));
  }

  public static Module mathModule() {
    return module(sequence(
        define(EQUAL, instructionIIB(BigInteger::equals)),
        define(MORE_THAN, instructionIIB((x, y) -> x.compareTo(y) < 0)),
        define(NEGATE, instructionII(BigInteger::negate)),
        define(ADD, instructionIII(BigInteger::add)),
        define(MULTIPLY, instructionIII(BigInteger::multiply)),
        define(DIVIDE_BY, instructionIII(flip(BigInteger::divide)))));
  }

  private static Definition define(Identifier name, Instruction instruction) {
    return definition(name, eager(instruction));
  }

  private static Instruction instructionII(
      Function<BigInteger, BigInteger> function) {
    return instruction(x -> stork(function.apply(javaInteger(x))));
  }

  private static Instruction instructionIII(
      BiFunction<BigInteger, BigInteger, BigInteger> function) {
    return instruction((x, y) -> stork(function.apply(javaInteger(x), javaInteger(y))));
  }

  private static Instruction instructionIIB(
      BiFunction<BigInteger, BigInteger, Boolean> function) {
    return instruction((x, y) -> stork(function.apply(javaInteger(x), javaInteger(y))));
  }
}
