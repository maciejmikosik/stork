package com.mikosik.stork.testing;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Computations.isComputable;

import java.util.function.Predicate;

import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;

public class MockingComputer implements Computer {
  private final Computer nextComputer;
  private final Predicate<String> mockPredicate;

  private MockingComputer(Predicate<String> mockPredicate, Computer nextComputer) {
    this.mockPredicate = mockPredicate;
    this.nextComputer = nextComputer;
  }

  public static Computer mocking(Predicate<String> mockPredicate, Computer nextComputer) {
    return new MockingComputer(mockPredicate, nextComputer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (mockPredicate.test(variable.name)) {
        return computation(Mock.mock(variable.name), computation.stack);
      } else {
        return nextComputer.compute(computation);
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
      return nextComputer.compute(computation);
    }
  }
}
