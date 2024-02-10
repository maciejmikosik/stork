package com.mikosik.stork.build.link.problem;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;

public class UndefinedIdentifier implements Problem {
  public final Identifier identifier;
  public final Definition definition;

  private UndefinedIdentifier(
      Definition definition,
      Identifier identifier) {
    this.definition = definition;
    this.identifier = identifier;
  }

  public static UndefinedIdentifier undefinedImport(
      Definition definition,
      Identifier identifier) {
    return new UndefinedIdentifier(definition, identifier);
  }

  public String toString() {
    return """
        undefined import
          definition: %s
          identifier: %s
        """.formatted(
        definition.identifier.name(),
        identifier.name());
  }
}
