package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Identifier;

public class FunctionNotDefined implements CannotLink {
  public final Identifier location;
  public final Identifier undefined;

  private FunctionNotDefined(
      Identifier location,
      Identifier undefined) {
    this.location = location;
    this.undefined = undefined;
  }

  public static FunctionNotDefined functionNotDefined(
      Identifier location,
      Identifier undefined) {
    return new FunctionNotDefined(
        location,
        undefined);
  }

  public String description() {
    return "function [%s] imports function [%s] which is not defined"
        .formatted(location.name(), undefined.name());
  }
}
