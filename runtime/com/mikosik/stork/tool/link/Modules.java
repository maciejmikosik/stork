package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;

import java.util.function.Function;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;

public class Modules {
  public static Function<Module, Module> each(Function<Definition, Definition> transform) {
    return module -> module(module.definitions.map(transform));
  }

  public static Function<Definition, Definition> changeDefinitionName(
      Function<String, String> change) {
    return definition -> definition(
        identifier(change.apply(definition.identifier.name())),
        definition.body);
  }
}
