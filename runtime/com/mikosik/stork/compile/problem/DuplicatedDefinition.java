package com.mikosik.stork.compile.problem;

import com.mikosik.stork.model.Identifier;

public class DuplicatedDefinition implements Problem {
  public final Identifier identifier;

  private DuplicatedDefinition(Identifier identifier) {
    this.identifier = identifier;
  }

  public static DuplicatedDefinition duplicatedDefinition(Identifier identifier) {
    return new DuplicatedDefinition(identifier);
  }

  public String toString() {
    return """
        duplicated definition
          identifier: %s
        """.formatted(identifier.name());
  }
}