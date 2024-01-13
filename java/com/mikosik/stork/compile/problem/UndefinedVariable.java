package com.mikosik.stork.compile.problem;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Variable;

public class UndefinedVariable implements Problem {
  public final Variable variable;
  public final Definition definition;

  private UndefinedVariable(
      Definition definition,
      Variable variable) {
    this.definition = definition;
    this.variable = variable;
  }

  public static UndefinedVariable undefinedVariable(
      Definition definition,
      Variable variable) {
    return new UndefinedVariable(definition, variable);
  }

  public String toString() {
    return """
        undefined variable
          definition: %s
          variable: %s
        """.formatted(
        definition.identifier.name(),
        variable.name);
  }
}
