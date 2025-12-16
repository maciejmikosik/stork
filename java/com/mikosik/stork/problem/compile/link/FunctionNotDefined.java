package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.problem.Description;

public class FunctionNotDefined extends CannotLink {
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

  public Description describe() {
    return description("function [%s] imports function [%s] which is not defined"
        .formatted(location.name(), undefined.name()));
  }
}
