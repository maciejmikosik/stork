package com.mikosik.stork.compute;

import static com.mikosik.stork.common.Check.check;

import java.math.BigInteger;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Integer;

public class Stack {
  private final Type type;
  private final Expression expression;
  private final Stack previous;

  private Stack(Type type, Expression expression, Stack previous) {
    this.type = type;
    this.expression = expression;
    this.previous = previous;
  }

  public static Stack stack() {
    return new Stack(Type.EMPTY, null, null);
  }

  private boolean isType(Type type) {
    return this.type == type;
  }

  public boolean isEmpty() {
    return isType(Type.EMPTY);
  }

  public boolean hasArgument() {
    return isType(Type.ARGUMENT);
  }

  public Expression argument() {
    check(hasArgument());
    return expression;
  }

  public Integer argumentInteger() {
    return (Integer) argument();
  }

  public BigInteger argumentIntegerJava() {
    return argumentInteger().value;
  }

  public Stack pushArgument(Expression argument) {
    return new Stack(Type.ARGUMENT, argument, this);
  }

  public boolean hasFunction() {
    return isType(Type.FUNCTION);
  }

  public Expression function() {
    check(hasFunction());
    return expression;
  }

  public Stack pushFunction(Expression function) {
    return new Stack(Type.FUNCTION, function, this);
  }

  public Stack pop() {
    check(!isEmpty());
    return previous;
  }

  private static enum Type {
    EMPTY, ARGUMENT, FUNCTION
  }
}
