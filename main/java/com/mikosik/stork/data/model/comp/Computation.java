package com.mikosik.stork.data.model.comp;

import static com.mikosik.stork.common.Chains.chainOf;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;

public class Computation implements Expression {
  public final Chain<Expression> stack;

  private Computation(Chain<Expression> stack) {
    this.stack = stack;
  }

  public static Expression computation(Chain<Expression> stack) {
    return new Computation(stack);
  }

  public static Expression computation(Expression expression) {
    return new Computation(chainOf(expression));
  }
}
