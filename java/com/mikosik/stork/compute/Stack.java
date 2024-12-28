package com.mikosik.stork.compute;

import com.mikosik.stork.model.Expression;

public abstract sealed class Stack {
  public static Stack stack() {
    return new Empty();
  }

  public boolean isEmpty() {
    return this instanceof Empty;
  }

  public boolean hasArgument() {
    return this instanceof Argument;
  }

  public Expression argument() {
    return switch (this) {
      case Argument argument -> argument.expression;
      default -> throw new RuntimeException();
    };
  }

  public Stack pushArgument(Expression argument) {
    return new Argument(argument, this);
  }

  public boolean hasFunction() {
    return this instanceof Function;
  }

  public Expression function() {
    return switch (this) {
      case Function function -> function.expression;
      default -> throw new RuntimeException();
    };
  }

  public Stack pushFunction(Expression function) {
    return new Function(function, this);
  }

  public Stack pop() {
    return switch (this) {
      case Frame frame -> frame.previous;
      default -> throw new RuntimeException();
    };
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
