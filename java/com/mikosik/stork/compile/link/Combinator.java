package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.common.TriFunction;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;

public enum Combinator implements Operator {
  /** I(x) = x */
  I(operator1(x -> x)),
  /** K(x)(y) = x */
  K(operator2((x, y) -> x)),
  /** S(x)(y)(z) = x(z)(y(z)) */
  S(operator3((x, y, z) -> ap(ap(x, z), ap(y, z)))),
  /** C(x)(y)(z) = x(z)(y) */
  C(operator3((x, y, z) -> ap(ap(x, z), y))),
  /** B(x)(y)(z) = x(y(z)) */
  B(operator3((x, y, z) -> ap(x, ap(y, z))));

  private final Operator logic;

  private Combinator(Operator logic) {
    this.logic = logic;
  }

  public Optional<Computation> compute(Stack stack) {
    return logic.compute(stack);
  }

  private static Operator operator1(Function<Expression, Expression> function) {
    return stack -> stack instanceof Argument argument
        ? Optional.of(computation(
            function.apply(argument.expression),
            argument.previous))
        : Optional.empty();
  }

  private static Operator operator2(BiFunction<Expression, Expression, Expression> function) {
    return stack -> stack instanceof Argument argumentA
        && argumentA.previous instanceof Argument argumentB
            ? Optional.of(computation(
                function.apply(
                    argumentA.expression,
                    argumentB.expression),
                argumentB.previous))
            : Optional.empty();
  }

  private static Operator operator3(
      TriFunction<Expression, Expression, Expression, Expression> function) {
    return stack -> stack instanceof Argument argumentA
        && argumentA.previous instanceof Argument argumentB
        && argumentB.previous instanceof Argument argumentC
            ? Optional.of(computation(
                function.apply(
                    argumentA.expression,
                    argumentB.expression,
                    argumentC.expression),
                argumentC.previous))
            : Optional.empty();
  }

  private static Expression ap(Expression function, Expression argument) {
    return application(function, argument);
  }
}
