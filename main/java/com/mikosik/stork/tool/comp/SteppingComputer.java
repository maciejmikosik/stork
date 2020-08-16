package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.comp.Switch.switchOn;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class SteppingComputer implements Computer {
  private final Computer nextComputer;

  private SteppingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer stepping(Computer nextComputer) {
    return new SteppingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    Stack stack = computation.stack;
    Expression expression = computation.expression;
    return switchOn(expression)
        .ifVariable(variable -> nextComputer.compute(computation))
        .ifApplication(application -> nextComputer.compute(computation))
        .ifLambda(lambda -> nextComputer.compute(computation))
        .ifNoun(noun -> switchOn(stack)
            .ifArgument(argument -> fail(format("cannot apply noun %s to argument %s",
                noun, argument)))
            .ifFunction(function -> nextComputer.compute(computation))
            .elseFail())
        .ifVerb(verb -> switchOn(stack)
            .ifArgument(argument -> nextComputer.compute(computation))
            .ifFunction(function -> nextComputer.compute(computation))
            .elseFail())
        .elseFail();
  }
}
