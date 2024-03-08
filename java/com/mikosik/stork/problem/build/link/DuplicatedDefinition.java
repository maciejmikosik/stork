package com.mikosik.stork.problem.build.link;

import com.mikosik.stork.model.Identifier;

public class DuplicatedDefinition implements CannotLink {
  public final Identifier identifier;

  private DuplicatedDefinition(Identifier identifier) {
    this.identifier = identifier;
  }

  public static DuplicatedDefinition duplicatedDefinition(Identifier identifier) {
    return new DuplicatedDefinition(identifier);
  }

  public String description() {
    return """
        duplicated definition
          identifier: %s
        """.formatted(identifier.name());
  }
}
