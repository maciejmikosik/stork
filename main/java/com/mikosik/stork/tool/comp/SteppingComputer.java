package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.comp.Argument.argument;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.data.model.comp.Switch.switchOn;
import static com.mikosik.stork.tool.common.Substitute.substitute;
import static java.lang.String.format;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Stack;

public class SteppingComputer implements Computer {
  private final Computer moduleComputer;

  private SteppingComputer(Computer moduleComputer) {
    this.moduleComputer = moduleComputer;
  }

  public static Computer stepping(Computer moduleComputer) {
    return new SteppingComputer(moduleComputer);
  }

  public Computation compute(Computation computation) {
    Stack stack = computation.stack;
    Expression expression = computation.expression;
    return switchOn(expression)
        .ifVariable(variable -> moduleComputer.compute(computation))
        .ifApplication(application -> computation(
            application.function,
            argument(application.argument, stack)))
        .ifLambda(lambda -> switchOn(stack)
            .ifArgument(argument -> computation(
                substitute(lambda, argument.expression),
                argument.stack))
            .elseFail())
        .ifNoun(noun -> switchOn(stack)
            .ifArgument(argument -> fail(format("cannot apply noun %s to argument %s",
                noun, argument)))
            .ifFunction(function -> computation(
                function.expression,
                argument(noun, function.stack)))
            .elseFail())
        .ifVerb(verb -> switchOn(stack)
            .ifArgument(argument -> switchOn(argument.expression)
                .ifApplication(application -> computation(
                    application,
                    function(verb, argument.stack)))
                .ifVariable(variable -> computation(
                    variable,
                    function(verb, argument.stack)))
                .elseReturn(() -> computation(
                    verb.apply(argument.expression),
                    argument.stack)))
            .ifFunction(function -> computation(
                function.expression,
                argument(verb, function.stack)))
            .elseFail())
        .elseFail();
  }
}
