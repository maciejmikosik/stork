package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.tool.common.InnateBuilder.innate;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Stack;

public class Combinators {
  /** I(x) = x */
  public static final Expression I = innate()
      .name("$I")
      .logic(stack -> computation(
          stack.argument(),
          stack.pop()))
      .build();

  /** K(x)(y) = x */
  public static final Expression K = innate()
      .name("$K")
      .logic(stack -> computation(
          stack.argument(),
          stack.pop().pop()))
      .build();

  /** S(x)(y)(z) = x(z)(y(z)) */
  public static final Expression S = innate()
      .name("$S")
      .logic(stack -> {
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
      })
      .build();

  /** C(x)(y)(z) = x(z)(y) */
  public static final Expression C = innate()
      .name("$C")
      .logic(stack -> {
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
      })
      .build();

  /** B(x)(y)(z) = x(y(z)) */
  public static final Expression B = innate()
      .name("$B")
      .logic(stack -> {
        Expression x = stack.argument();
        stack = stack.pop();
        Expression y = stack.argument();
        stack = stack.pop();
        Expression z = stack.argument();
        stack = stack.pop();
        stack = stack.pushArgument(application(y, z));
        return computation(x, stack);
      })
      .build();

  /** Y(f) = f(Y(f)) */
  public static final Expression Y = innate()
      .name("$Y")
      .logic(new Innate() {
        public Computation compute(Stack stack) {
          Expression f = stack.argument();
          stack = stack.pop();
          stack = stack.pushArgument(application(Y, f));
          return computation(f, stack);
        }
      })
      .build();
}
