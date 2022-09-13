package com.mikosik.stork.tool.common;

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
}
