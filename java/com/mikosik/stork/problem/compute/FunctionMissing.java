package com.mikosik.stork.problem.compute;

import static com.mikosik.stork.common.Description.description;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.model.Identifier;

public class FunctionMissing extends CannotCompute {
  private final Identifier function;

  private FunctionMissing(Identifier function) {
    this.function = function;
  }

  public static FunctionMissing functionMissing(Identifier function) {
    return new FunctionMissing(function);
  }

  public Description describe() {
    return description("function [%s] is missing"
        .formatted(function.name()));
  }
}
