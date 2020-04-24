package com.mikosik.stork.tool.run;

import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.Expressions.substitute;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.tool.Binary;

public class RecursiveRunner implements Runner {
  private final Binary binary;

  private RecursiveRunner(Binary binary) {
    this.binary = binary;
  }

  public static Runner recursiveRunner(Binary binary) {
    return new RecursiveRunner(binary);
  }

  public Expression run(Expression expression) {
    return switchOn(expression)
        .ifVariable(variable -> run(binary.table.get(variable.name)))
        .ifNoun(noun -> noun)
        .ifApplication(application -> run(application))
        .ifLambda(lambda -> lambda)
        .ifCore(core -> core)
        .elseFail();
  }

  private Expression run(Application application) {
    return switchOn(run(application.function))
        .ifLambda(lambda -> run(substitute(lambda, application.argument)))
        .ifCore(core -> run(core.apply(run(application.argument))))
        .elseFail();
  }
}
