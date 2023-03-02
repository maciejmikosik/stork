package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.change.Changes.onEachDefinition;

import java.util.Map;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class Redefine {
  public static Module redefine(Module updated, Module original) {
    Map<Identifier, Definition> updates = updated.definitions
        .toHashMap(
            definition -> definition.identifier,
            definition -> definition);
    return onEachDefinition(definition -> updates.getOrDefault(definition.identifier, definition))
        .apply(original);
  }
}
