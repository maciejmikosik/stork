package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Variable;

public class UnboundVariable {
  public static Problem unboundVariable(
      Identifier location,
      Variable variable) {
    return problem("unbound variable")
        .location(location)
        .object(variable)
        .build();
  }
}
