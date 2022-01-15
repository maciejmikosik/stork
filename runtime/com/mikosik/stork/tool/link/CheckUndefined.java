package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.common.Morph.morphIdentifiers;
import static com.mikosik.stork.tool.common.Morph.morphVariables;

import java.util.Set;

import com.mikosik.stork.model.Module;

public class CheckUndefined {
  // TODO throw dedicated exception
  public static void checkUndefined(Module module) {
    Set<String> defined = module.definitions
        .map(definition -> definition.identifier.name)
        .toHashSet();
    morphVariables(variable -> {
      throw new RuntimeException(variable.name);
    }).in(module);
    morphIdentifiers(identifier -> {
      if (!defined.contains(identifier.name)) {
        throw new RuntimeException(identifier.name);
      } else {
        return identifier;
      }
    }).in(module);
  }
}
