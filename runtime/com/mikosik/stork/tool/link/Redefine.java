package com.mikosik.stork.tool.link;

import java.util.Map;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.common.Traverser;

public class Redefine {
  public static Module redefine(Module updated, Module original) {
    Map<String, Definition> updates = updated.definitions
        .toHashMap(
            definition -> definition.variable.name,
            definition -> definition);
    return new Traverser() {
      public Definition traverse(Definition definition) {
        return updates.getOrDefault(definition.variable.name, definition);
      }
    }.traverse(original);
  }
}
