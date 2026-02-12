package com.mikosik.stork.problem.compute;

import com.mikosik.stork.model.Identifier;

public class FunctionMissing extends CannotCompute {
  public final Identifier function;

  private FunctionMissing(Identifier function) {
    this.function = function;
  }

  public static FunctionMissing functionMissing(Identifier function) {
    return new FunctionMissing(function);
  }
}
