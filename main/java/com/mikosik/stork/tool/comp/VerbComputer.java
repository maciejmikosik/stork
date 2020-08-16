package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
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
    return requiresComputing(argument.expression)
        ? computation(
            argument.expression,
            function(verb, argument.stack))
        : computation(
            verb.apply(argument.expression),
            argument.stack);
  }

  private static boolean requiresComputing(Expression expression) {
    return expression instanceof Variable
        || expression instanceof Application;
  }
}
