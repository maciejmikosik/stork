package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Logic.flip;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.NamedInstruction.name;
import static com.mikosik.stork.tool.common.Bridge.stork;
import static com.mikosik.stork.tool.common.Instructions.instruction;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Module;

public class MathModule {
  public static Module mathModule() {
    return module(chainOf(
        define(
            "stork.integer.native.EQUAL",
            instructionIIB(BigInteger::equals)),
        define(
            "stork.integer.native.MORETHAN",
            instructionIIB((x, y) -> x.compareTo(y) < 0)),
        define(
            "stork.integer.native.NEGATE",
            instructionII(BigInteger::negate)),
        define(
            "stork.integer.native.ADD",
            instructionIII(BigInteger::add)),
        define(
            "stork.integer.native.MULTIPLY",
            instructionIII(BigInteger::multiply)),
        define(
            "stork.integer.native.DIVIDEBY",
            instructionIII(flip(BigInteger::divide)))));
  }

  private static Definition define(String name, Instruction instruction) {
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
