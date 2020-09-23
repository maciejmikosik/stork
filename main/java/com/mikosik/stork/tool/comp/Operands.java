package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Integer;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Stack;

public class Operands {
  private Stack stack;

  private Operands(Stack stack) {
    this.stack = stack;
  }

  public static Operands operands(Stack stack) {
    return new Operands(stack);
  }

  public Expression next() {
    Argument argument = (Argument) stack;
    stack = argument.stack;
    return argument.expression;
  }

  public Integer nextInteger() {
    return (Integer) next();
  }

  public BigInteger nextJavaBigInteger() {
    return asJavaBigInteger(nextInteger());
  }

  public Stack stack() {
    return stack;
  }
}
