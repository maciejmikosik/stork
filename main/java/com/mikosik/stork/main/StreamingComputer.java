package com.mikosik.stork.main;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Computations.isComputable;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.tool.comp.Computer;

// TODO replace this computer by making writeByte eager
public class StreamingComputer implements Computer {
  private final Computer nextComputer;

  private StreamingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer streaming(Computer nextComputer) {
    return new StreamingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (variable.name.equals("writeByte")) {
        Argument argument = (Argument) computation.stack;
        Expression expression = argument.expression;
        return isComputable(expression)
            ? computation(
                expression,
                function(computation.expression, argument.stack))
            : nextComputer.compute(computation);
      }
    }
    return nextComputer.compute(computation);
  }
}
