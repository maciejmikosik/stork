package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compile.link.Bridge.stork;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Integer.integer;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Operator;

public enum MathOperator implements Operator {
  EQUAL(operatorIIB(BigInteger::equals)),
  COMPARE(operatorIIB((x, y) -> x.compareTo(y) < 0)),
  NEGATE(operatorII(BigInteger::negate)),
  ADD(operatorIII(BigInteger::add)),
  MULTIPLY(operatorIII(BigInteger::multiply)),
  DIVIDE(operatorIII(BigInteger::divide));

  private final Operator logic;

  private MathOperator(Operator logic) {
    this.logic = logic;
  }

  public Optional<Computation> compute(Stack stack) {
    return logic.compute(stack);
  }

  private static Operator operatorII(Function<BigInteger, BigInteger> function) {
    return stack -> stack instanceof Argument argument
        && argument.expression instanceof Integer integer
            ? Optional.of(computation(
                integer(function.apply(integer.value)),
                argument.previous))
            : Optional.empty();
  }

  private static Operator operatorIII(BiFunction<BigInteger, BigInteger, BigInteger> function) {
    return stack -> stack instanceof Argument argumentA
        && argumentA.expression instanceof Integer integerA
        && argumentA.previous instanceof Argument argumentB
        && argumentB.expression instanceof Integer integerB
            ? Optional.of(computation(
                integer(function.apply(integerA.value, integerB.value)),
                argumentB.previous))
            : Optional.empty();
  }

  private static Operator operatorIIB(BiFunction<BigInteger, BigInteger, Boolean> function) {
    return stack -> stack instanceof Argument argumentA
        && argumentA.expression instanceof Integer integerA
        && argumentA.previous instanceof Argument argumentB
        && argumentB.expression instanceof Integer integerB
            ? Optional.of(computation(
                stork(function.apply(integerA.value, integerB.value)),
                argumentB.previous))
            : Optional.empty();
  }
}
