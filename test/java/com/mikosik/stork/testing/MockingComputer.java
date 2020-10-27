package com.mikosik.stork.testing;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.compute.Computer;

public class MockingComputer implements Computer {
  private final Computer computer;

  private MockingComputer(Computer computer) {
    this.computer = computer;
  }

  public static Computer mocking(Computer computer) {
    return new MockingComputer(computer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (variable.name.length() == 1) {
        return computation(Mock.mock(variable.name), computation.stack);
      } else {
        return computer.compute(computation);
      }
    } else if (computation.expression instanceof Mock
        && computation.stack instanceof Argument) {
      Mock mock = (Mock) computation.expression;
      Argument argument = (Argument) computation.stack;
      if (isComputable(argument.expression)) {
        return computation(
            argument.expression,
            function(computation.expression, argument.stack));
      } else {
        return computation(
            mock.apply(argument.expression),
            argument.stack);
      }
    } else {
      return computer.compute(computation);
    }
  }

  private static boolean isComputable(Expression expression) {
    return expression instanceof Variable
        || expression instanceof Application;
  }
}
