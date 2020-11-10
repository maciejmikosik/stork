package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.compute.Operands.operands;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Opcode;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class OpcodingComputer implements Computer {
  private final Computer computer;

  private OpcodingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer opcoding(Computer computer) {
    return new OpcodingComputer(computer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Opcode
        ? compute((Opcode) computation.expression, computation.stack)
        : computer.compute(computation);
  }

  private Computation compute(Opcode opcode, Stack stack) {
    switch (opcode) {
      case ARG_1:
        return handleArg1(stack);
      case ARG_2:
        return handleArg2(stack);
      case NEGATE:
        return handleNegate(stack);
      case ADD:
        return handleAdd(stack);
      case EQUAL:
        return handleEqual(stack);
      case MORE_THAN:
        return handleMoreThan(stack);
      default:
        throw new RuntimeException();
    }
  }

  private static Computation handleArg1(Stack stack) {
    Operands operands = operands(stack);
    Expression function = operands.next();
    Expression argument = operands.next();
    Stack newStack = operands.stack();
    return computation(
        argument,
        function(
            function,
            newStack));
  }

  private static Computation handleArg2(Stack stack) {
    Operands operands = operands(stack);
    Expression function = operands.next();
    Expression argumentA = operands.next();
    Expression argumentB = operands.next();
    Stack newStack = operands.stack();
    return computation(
        argumentB,
        function(
            application(function, argumentA),
            newStack));
  }

  private static Computation handleNegate(Stack stack) {
    return handle(stack, x -> integer(x.negate()));
  }

  private static Computation handleAdd(Stack stack) {
    return handle(stack, (x, y) -> integer(x.add(y)));
  }

  private static Computation handleEqual(Stack stack) {
    return handle(stack, (x, y) -> asStorkBoolean(x.equals(y)));
  }

  private static Computation handleMoreThan(Stack stack) {
    return handle(stack, (x, y) -> asStorkBoolean(y.compareTo(x) > 0));
  }

  private static Computation handle(
      Stack stack,
      Function<BigInteger, Expression> logic) {
    Operands operands = operands(stack);
    return computation(
        logic.apply(operands.nextJavaBigInteger()),
        operands.stack());
  }

  private static Computation handle(
      Stack stack,
      BiFunction<BigInteger, BigInteger, Expression> logic) {
    Operands operands = operands(stack);
    return computation(
        logic.apply(
            operands.nextJavaBigInteger(),
            operands.nextJavaBigInteger()),
        operands.stack());
  }
}
