package com.mikosik.stork.data.model;

import static com.mikosik.stork.common.Chains.chainOf;

import com.mikosik.stork.common.Chain;

public class Running implements Expression {
  public final Chain<Expression> stack;

  private Running(Chain<Expression> stack) {
    this.stack = stack;
  }

  public static Expression running(Chain<Expression> stack) {
    return new Running(stack);
  }

  public static Expression running(Expression expression) {
    return new Running(chainOf(expression));
  }
}
