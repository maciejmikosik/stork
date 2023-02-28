package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.Modules.each;

import java.util.Map;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;

public class Redefine {
  public static Module redefine(Module updated, Module original) {
    Map<String, Definition> updates = updated.definitions
        .toHashMap(
            definition -> definition.identifier.name,
            definition -> definition);
    return each(definition -> updates.getOrDefault(definition.identifier.name, definition))
        .apply(original);
  }
}
