package com.mikosik.stork.tool.run;

import static com.mikosik.stork.data.model.ExpressionSwitcher.expressionSwitcherReturning;
import static com.mikosik.stork.tool.run.Substitute.substitute;

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
    return expressionSwitcherReturning(Expression.class)
        .ifVariable(variable -> run(binary.table.get(variable.name)))
        .ifPrimitive(primitive -> primitive)
        .ifApplication(application -> run(application))
        .ifLambda(lambda -> lambda)
        .ifCore(core -> core)
        .apply(expression);
  }

  private Expression run(Application application) {
    Expression function = run(application.function);
    return expressionSwitcherReturning(Expression.class)
        .ifLambda(lambda -> run(substitute(
            lambda.body,
            lambda.parameter,
            application.argument)))
        .ifCore(core -> run(core.apply(run(application.argument))))
        .apply(function);
  }
}
