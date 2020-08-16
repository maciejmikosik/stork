package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Computations.isComputable;

import com.mikosik.stork.data.model.Verb;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;

public class VerbComputer implements Computer {
  private final Computer nextComputer;

  private VerbComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer verb(Computer nextComputer) {
    return new VerbComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Verb
        && computation.stack instanceof Argument
            ? compute(
                (Verb) computation.expression,
                (Argument) computation.stack)
            : nextComputer.compute(computation);
  }

  private Computation compute(Verb verb, Argument argument) {
    return isComputable(argument.expression)
        ? computation(
            argument.expression,
            function(verb, argument.stack))
        : computation(
            verb.apply(argument.expression),
            argument.stack);
  }
}
