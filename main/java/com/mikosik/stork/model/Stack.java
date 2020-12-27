package com.mikosik.stork.model;

import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Check.check;

import java.math.BigInteger;

import com.mikosik.stork.common.Chain;

public class Stack {
  private final Chain<Frame> frames;

  protected Stack(Chain<Frame> frames) {
    this.frames = frames;
  }

  public static Stack stack() {
    return new Stack(empty());
  }

  private boolean hasFrames() {
    return !frames.isEmpty();
  }

  private Frame lastFrame() {
    return frames.head();
  }

  private boolean has(Type type) {
    return hasFrames() && lastFrame().type == type;
  }

  private Stack push(Frame frame) {
    return new Stack(frames.add(frame));
  }

  public boolean hasArgument() {
    return has(Type.ARGUMENT);
  }

  public Expression argument() {
    check(hasArgument());
    return lastFrame().expression;
  }

  public Integer argumentInteger() {
    return (Integer) argument();
  }

  public BigInteger argumentIntegerJava() {
    return argumentInteger().value;
  }

  public Stack pushArgument(Expression argument) {
    return push(new Frame(Type.ARGUMENT, argument));
  }

  public boolean hasFunction() {
    return has(Type.FUNCTION);
  }

  public Expression function() {
    check(hasFunction());
    return lastFrame().expression;
  }

  public Stack pushFunction(Expression function) {
    return push(new Frame(Type.FUNCTION, function));
  }

  public Stack pop() {
    return new Stack(frames.tail());
  }

  private static enum Type {
    ARGUMENT, FUNCTION
  }

  private static class Frame {
    public final Type type;
    public final Expression expression;

    public Frame(Type type, Expression expression) {
      this.type = type;
      this.expression = expression;
    }
  }
}
