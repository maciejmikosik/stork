package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.tool.common.Constants.FALSE;
import static com.mikosik.stork.tool.common.Constants.TRUE;
import static com.mikosik.stork.tool.common.Eager.eager;
import static com.mikosik.stork.tool.common.Instructions.instruction1;
import static com.mikosik.stork.tool.common.Instructions.instruction2;

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
        define("stork.integer.negate", 1, instructionII(BigInteger::negate)),
        define("stork.integer.add", 2, instructionIII(BigInteger::add)),
        define("stork.integer.equal", 2, instructionIIB(BigInteger::equals)),
        define("stork.integer.moreThan", 2, instructionIIB((x, y) -> x.compareTo(y) < 0))));
  }

  private static Definition define(String name, int parameters, Instruction instruction) {
    return definition(identifier(name), eager(parameters, instruction));
  }

  private static Instruction instructionII(
      Function<BigInteger, BigInteger> function) {
    return instruction1(x -> toStork(function.apply(toJavaInteger(x))));
  }

  private static Instruction instructionIII(
      BiFunction<BigInteger, BigInteger, BigInteger> function) {
    return instruction2(x -> y -> toStork(function.apply(toJavaInteger(x), toJavaInteger(y))));
  }

  private static Instruction instructionIIB(
      BiFunction<BigInteger, BigInteger, Boolean> function) {
    return instruction2(x -> y -> toStork(function.apply(toJavaInteger(x), toJavaInteger(y))));
  }

  private static Expression toStork(Boolean value) {
    return value ? TRUE : FALSE;
  }

  private static Expression toStork(BigInteger value) {
    return integer(value);
  }

  private static BigInteger toJavaInteger(Expression expression) {
    return ((Integer) expression).value;
  }
}
