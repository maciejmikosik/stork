package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;

public class VariableCannotBeBound implements CannotLink {
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

  public String description() {
    return "function [%s] uses variable [%s] which doesn't match any lambda parameter, local function or import"
        .formatted(location.name(), variable.name);
  }
}
