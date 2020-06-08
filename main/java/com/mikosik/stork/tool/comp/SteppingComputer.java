package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Computation.computation;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.common.Ascend.ascend;
import static com.mikosik.stork.tool.common.Substitute.substitute;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Computation;
import com.mikosik.stork.data.model.Expression;

public class SteppingComputer implements Computer {
  private final Computer moduleComputer;

  private SteppingComputer(Computer moduleComputer) {
    this.moduleComputer = moduleComputer;
  }

  public static Computer stepping(Computer moduleComputer) {
    return new SteppingComputer(moduleComputer);
  }

  public Expression compute(Expression expression) {
    return switchOn(expression)
        .ifComputation(computation -> compute(computation))
        .elseReturn(() -> computation(expression));
  }

  private Expression compute(Computation computation) {
    return computation.stack.visit(
        (head, tail) -> compute(head, tail),
        () -> throwing(new RuntimeException()));
  }

  private Expression compute(Expression expression, Chain<Expression> stack) {
    return switchOn(expression)
        .ifVariable(variable -> computation(add(moduleComputer.compute(variable), stack)))
        .ifApplication(application -> switchOn(application.function)
            .ifVerb(verb -> switchOn(application.argument)
                .ifVariable(argument -> computation(add(argument, add(expression, stack))))
                .ifApplication(argument -> computation(add(argument, add(expression, stack))))
                .elseReturn(() -> computation(add(verb.apply(application.argument), stack))))
            .ifVariable(function -> computation(add(function, add(expression, stack))))
            .ifApplication(function -> computation(add(function, add(expression, stack))))
            .ifNoun(noun -> fail("cannot apply noun " + noun))
            .ifLambda(lambda -> computation(add(substitute(lambda, application.argument), stack)))
            .elseFail())
        .elseReturn(() -> stack.visit(
            (head, tail) -> computation(add(ascend(expression, head), tail)),
            () -> expression));
  }
}
