package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Logic.flip;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Instruction.instruction;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.NamedInstruction.name;
import static com.mikosik.stork.tool.common.Bridge.stork;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Module;

public class MathModule {
  public static final Identifier EQUAL = identifier("stork.integer.native.EQUAL");
  public static final Identifier MORETHAN = identifier("stork.integer.native.MORETHAN");
  public static final Identifier NEGATE = identifier("stork.integer.native.NEGATE");
  public static final Identifier ADD = identifier("stork.integer.native.ADD");
  public static final Identifier MULTIPLY = identifier("stork.integer.native.MULTIPLY");
  public static final Identifier DIVIDEBY = identifier("stork.integer.native.DIVIDEBY");

  public static Module mathModule() {
    return module(chainOf(
        define(EQUAL, instructionIIB(BigInteger::equals)),
        define(MORETHAN, instructionIIB((x, y) -> x.compareTo(y) < 0)),
        define(NEGATE, instructionII(BigInteger::negate)),
        define(ADD, instructionIII(BigInteger::add)),
        define(MULTIPLY, instructionIII(BigInteger::multiply)),
        define(DIVIDEBY, instructionIII(flip(BigInteger::divide)))));
  }

  private static Definition define(Identifier name, Instruction instruction) {
    return definition(name, eager(name(name, instruction)));
  }

  private static Instruction instructionII(
      Function<BigInteger, BigInteger> function) {
    return instruction(x -> stork(function.apply(java(x))));
  }

  private static Instruction instructionIII(
      BiFunction<BigInteger, BigInteger, BigInteger> function) {
    return instruction((x, y) -> stork(function.apply(java(x), java(y))));
  }

  private static Instruction instructionIIB(
      BiFunction<BigInteger, BigInteger, Boolean> function) {
    return instruction((x, y) -> stork(function.apply(java(x), java(y))));
  }

  private static BigInteger java(Expression expression) {
    return ((Integer) expression).value;
  }
}
