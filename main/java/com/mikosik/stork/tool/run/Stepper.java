package com.mikosik.stork.tool.run;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.Throwables.throwing;
import static com.mikosik.stork.data.model.Running.running;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.common.Ascend.ascend;
import static com.mikosik.stork.tool.common.Substitute.substitute;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Running;

public class Stepper implements Runner {
  private final Runner moduleRunner;

  private Stepper(Runner moduleRunner) {
    this.moduleRunner = moduleRunner;
  }

  public static Runner stepper(Runner moduleRunner) {
    return new Stepper(moduleRunner);
  }

  public Expression run(Expression expression) {
    return switchOn(expression)
        .ifRunning(running -> run(running))
        .elseReturn(() -> running(expression));
  }

  private Expression run(Running running) {
    return running.stack.visit(
        (head, tail) -> run(head, tail),
        () -> throwing(new RuntimeException()));
  }

  private Expression run(Expression expression, Chain<Expression> stack) {
    return switchOn(expression)
        .ifVariable(variable -> running(add(moduleRunner.run(variable), stack)))
        .ifApplication(application -> switchOn(application.function)
            .ifVerb(verb -> switchOn(application.argument)
                .ifVariable(argument -> running(add(argument, add(expression, stack))))
                .ifApplication(argument -> running(add(argument, add(expression, stack))))
                .elseReturn(() -> running(add(verb.apply(application.argument), stack))))
            .ifVariable(function -> running(add(function, add(expression, stack))))
            .ifApplication(function -> running(add(function, add(expression, stack))))
            .ifNoun(noun -> fail("cannot apply noun " + noun))
            .ifLambda(lambda -> running(add(substitute(lambda, application.argument), stack)))
            .elseFail())
        .elseReturn(() -> stack.visit(
            (head, tail) -> running(add(ascend(expression, head), tail)),
            () -> expression));
  }
}
