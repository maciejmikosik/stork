package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.common.Innates.rename;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;

public class Combinators {
  /** I(x) = x */
  public static final Innate I = name("I", stack -> {
    return computation(
        stack.argument(),
        stack.pop());
  });

  /** K(x)(y) = x */
  public static final Innate K = name("K", stack -> {
    return computation(
        stack.argument(),
        stack.pop().pop());
  });

  /** S(x)(y)(z) = x(z)(y(z)) */
  public static final Innate S = name("S", stack -> {
    Expression x = stack.argument();
    stack = stack.pop();
    Expression y = stack.argument();
    stack = stack.pop();
    Expression z = stack.argument();
    stack = stack.pop();
    stack = stack
        .pushArgument(application(y, z))
        .pushArgument(z);
    return computation(x, stack);
  });

  /** C(x)(y)(z) = x(z)(y) */
  public static final Innate C = name("C", stack -> {
    Expression x = stack.argument();
    stack = stack.pop();
    Expression y = stack.argument();
    stack = stack.pop();
    Expression z = stack.argument();
    stack = stack.pop();
    stack = stack
        .pushArgument(y)
        .pushArgument(z);
    return computation(x, stack);
  });

  /** B(x)(y)(z) = x(y(z)) */
  public static final Innate B = name("B", stack -> {
    Expression x = stack.argument();
    stack = stack.pop();
    Expression y = stack.argument();
    stack = stack.pop();
    Expression z = stack.argument();
    stack = stack.pop();
    stack = stack.pushArgument(application(y, z));
    return computation(x, stack);
  });

  private static Innate name(String localName, Innate innate) {
    return rename("stork.innate.combinator." + localName, innate);
  }
}
