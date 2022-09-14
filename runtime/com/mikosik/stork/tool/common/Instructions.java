package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Eager.eager;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Instruction;

public class Instructions {
  public static Instruction instruction1(
      Function<Expression, Expression> function) {
    return x -> function.apply(x);
  }

  public static Instruction instruction2(
      Function<Expression, Function<Expression, Expression>> function) {
    return x -> (Instruction) y -> function.apply(x).apply(y);
  }

  public static Instruction instruction3(
      Function<Expression, Function<Expression, Function<Expression, Expression>>> function) {
    return x -> (Instruction) y -> (Instruction) z -> function.apply(x).apply(y).apply(z);
  }

  public static Expression eagerDeep(Instruction instruction) {
    return eager(eagerDeepResult(instruction));
  }

  private static Instruction eagerDeepResult(Instruction instruction) {
    return argument -> eagerDeepIfInstruction(instruction.apply(argument));
  }

  private static Expression eagerDeepIfInstruction(Expression function) {
    return function instanceof Instruction
        ? eagerDeep((Instruction) function)
        : function;
  }

  public static Instruction name(Expression instructionName, Instruction instruction) {
    return new Instruction() {
      public Expression name = instructionName;

      public Expression apply(Expression argument) {
        Expression applied = instruction.apply(argument);
        return applied instanceof Instruction
            ? name(application(name, argument), (Instruction) applied)
            : applied;
      }
    };
  }
}
