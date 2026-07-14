package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;

public class UnboundVariable extends CannotLink {
  public final Identifier location;
  public final Variable variable;

  private UnboundVariable(
      Identifier location,
      Variable variable) {
    this.location = location;
    this.variable = variable;
  }

  public static UnboundVariable unboundVariable(
      Identifier location,
      Variable variable) {
    return new UnboundVariable(
        location,
        variable);
  }
}
