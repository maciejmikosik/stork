package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.model.Module.module;
import static java.util.stream.Collectors.toList;

import java.util.Map;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;

public class Redefine {
  public static Module redefine(Module updated, Module original) {
    Map<String, Definition> updates = updated.definitions
        .toHashMap(
            definition -> definition.identifier.name,
            definition -> definition);
    return module(chainFrom(original.definitions.stream()
        .map(definition -> updates.getOrDefault(definition.identifier.name, definition))
        .collect(toList())));
  }
}
