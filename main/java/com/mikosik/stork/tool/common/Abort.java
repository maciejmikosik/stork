package com.mikosik.stork.tool.common;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Switch.switchOn;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.comp.Computation;
import com.mikosik.stork.data.model.comp.Empty;

public class Abort {
  /**
   * @return current state of {@code computation} by moving up stack
   */
  public static Expression abort(Computation computation) {
    Computation aborting = computation;
    while (!(aborting.stack instanceof Empty)) {
      aborting = moveUpStack(aborting);
    }
    return aborting.expression;
  }

  private static Computation moveUpStack(Computation computation) {
    return switchOn(computation.stack)
        .ifArgument(argument -> computation(
            application(computation.expression, argument.expression),
            argument.stack))
        .ifFunction(function -> computation(
            application(function.expression, computation.expression),
            function.stack))
        .elseFail();
  }
}
