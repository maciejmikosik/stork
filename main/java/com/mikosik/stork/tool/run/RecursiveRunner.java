package com.mikosik.stork.tool.run;

import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.common.Substitute.substitute;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;

public class RecursiveRunner implements Runner {
  private final Runner moduleRunner;

  private RecursiveRunner(Runner moduleRunner) {
    this.moduleRunner = moduleRunner;
  }

  public static Runner recursiveRunner(Runner moduleRunner) {
    return new RecursiveRunner(moduleRunner);
  }

  public Expression run(Expression expression) {
    return switchOn(expression)
        .ifVariable(variable -> run(moduleRunner.run(variable)))
        .ifNoun(noun -> noun)
        .ifApplication(application -> run(application))
        .ifLambda(lambda -> lambda)
        .ifVerb(verb -> verb)
        .elseFail();
  }

  private Expression run(Application application) {
    return switchOn(run(application.function))
        .ifLambda(lambda -> run(substitute(lambda, application.argument)))
        .ifVerb(verb -> run(verb.apply(run(application.argument))))
        .elseFail();
  }
}
