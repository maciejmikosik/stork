package com.mikosik.stork.build.link.problem;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;

public class UndefinedImport implements CannotLink {
  public final Identifier identifier;
  public final Definition definition;

  private UndefinedImport(
      Definition definition,
      Identifier identifier) {
    this.definition = definition;
    this.identifier = identifier;
  }

  public static UndefinedImport undefinedImport(
      Definition definition,
      Identifier identifier) {
    return new UndefinedImport(definition, identifier);
  }

  public String description() {
    return """
        undefined import
          definition: %s
          identifier: %s
        """.formatted(
        definition.identifier.name(),
        identifier.name());
  }
}
