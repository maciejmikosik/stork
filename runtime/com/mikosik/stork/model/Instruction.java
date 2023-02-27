package com.mikosik.stork.model;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.TriFunction;

public interface Instruction extends Expression {
  Expression apply(Expression argument);

  static Instruction instruction(
      Function<Expression, Expression> function) {
    return x -> function.apply(x);
  }

  static Instruction instruction(
      BiFunction<Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> function.apply(x, y);
  }

  static Instruction instruction(
      TriFunction<Expression, Expression, Expression, Expression> function) {
    return x -> (Instruction) y -> (Instruction) z -> function.apply(x, y, z);
  }
}
