package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;

import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;

public class Modules {
  public static Function<Module, Module> each(Function<Definition, Definition> transform) {
    return module -> module(module.definitions.map(transform));
  }

  public static Function<Definition, Definition> onIdentifier(
      Function<Identifier, Identifier> change) {
    return definition -> definition(
        change.apply(definition.identifier),
        definition.body);
  }

  public static Function<Identifier, Identifier> onNamespace(
      Function<Namespace, Namespace> change) {
    return identifier -> identifier(
        change.apply(identifier.namespace),
        identifier.variable);
  }
}
