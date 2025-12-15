package com.mikosik.stork.compile.link;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Operator;

public enum StackOperator implements Operator {
  EAGER {
    public Computation compute(Stack stack) {
      int nArguments = 2;
      var arguments = new Expression[nArguments];
      for (int iArgument = 0; iArgument < nArguments; iArgument++) {
        if (stack instanceof Argument argument) {
          arguments[iArgument] = argument.expression;
          stack = argument.previous;
        } else {
          throw cannotCompute();
        }
      }
      return computation(
          arguments[1],
          stack.pushFunction(arguments[0]));
    }
  };
}
