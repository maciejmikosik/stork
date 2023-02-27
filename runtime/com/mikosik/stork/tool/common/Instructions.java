package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Eager.eager;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.TriFunction;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Instruction;

public class Instructions {
  public static Instruction instruction(
      Function<Expression, Expression> function) {
    return x -> function.apply(x);
  }

  public static Instruction instruction(
      BiFunction<Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> function.apply(x, y);
  }

  public static Instruction instruction(
      TriFunction<Expression, Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> (Instruction) z -> function.apply(x, y, z);
  }

  public static Expression eagerDeep(Instruction instruction) {
    return eager(eagerDeepResult(instruction));
  }

  private static Instruction eagerDeepResult(Instruction instruction) {
    return argument -> eagerDeepIfInstruction(instruction.apply(argument));
  }

  private static Expression eagerDeepIfInstruction(Expression function) {
    return function instanceof Instruction instruction
        ? eagerDeep(instruction)
        : function;
  }
}
