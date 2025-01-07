package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compute.Computation.computation;

import java.util.Optional;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;

public enum StackOperator implements Operator {
  EAGER {
    public Optional<Computation> compute(Stack stack) {
      return stack instanceof Argument argumentA
          && argumentA.previous instanceof Argument argumentB
              ? Optional.of(compute(
                  argumentA.expression,
                  argumentB.expression,
                  argumentB.previous))
              : Optional.empty();
    }

    private static Computation compute(
        Expression argumentA,
        Expression argumentB,
        Stack stack) {
      return computation(argumentB, stack.pushFunction(argumentA));
    }
  };
}
