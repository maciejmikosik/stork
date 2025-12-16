package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.Description;

public class VariableCannotBeBound extends CannotLink {
  public final Identifier location;
  public final Variable variable;

  private VariableCannotBeBound(
      Identifier location,
      Variable variable) {
    this.location = location;
    this.variable = variable;
  }

  public static VariableCannotBeBound variableCannotBeBound(
      Identifier location,
      Variable variable) {
    return new VariableCannotBeBound(
        location,
        variable);
  }

  public Description describe() {
    return description(
        "function [%s] uses variable [%s] which doesn't match any lambda parameter, local function or import"
            .formatted(location.name(), variable.name));
  }
}
