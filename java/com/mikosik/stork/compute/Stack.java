package com.mikosik.stork.compute;

import com.mikosik.stork.model.Expression;

public abstract sealed class Stack {
  public static Stack stack() {
    return new Empty();
  }

  public Stack pushArgument(Expression argument) {
    return new Argument(argument, this);
  }

  public Stack pushFunction(Expression function) {
    return new Function(function, this);
  }

  public static final class Empty extends Stack {}

  public static sealed class Frame extends Stack {
    public final Expression expression;
    public final Stack previous;

    private Frame(Expression expression, Stack previous) {
      this.expression = expression;
      this.previous = previous;
    }
  }

  public static final class Argument extends Frame {
    private Argument(Expression expression, Stack previous) {
      super(expression, previous);
    }
  }

  public static final class Function extends Frame {
    private Function(Expression expression, Stack previous) {
      super(expression, previous);
    }
  }
}
