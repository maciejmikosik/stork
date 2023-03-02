package com.mikosik.stork.tool.link;

import static com.mikosik.stork.tool.link.Changes.changeIdentifier;
import static com.mikosik.stork.tool.link.Changes.changeVariable;
import static com.mikosik.stork.tool.link.Changes.inModule;

import java.util.Set;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class CheckUndefined {
  // TODO throw dedicated exception
  public static void checkUndefined(Module module) {
    inModule(changeVariable(variable -> {
      throw new RuntimeException(variable.name);
    })).apply(module);
    Set<Identifier> defined = module.definitions
        .map(definition -> definition.identifier)
        .toHashSet();
    inModule(changeIdentifier(identifier -> {
      if (!defined.contains(identifier)) {
        throw new RuntimeException(identifier.name());
      } else {
        return identifier;
      }
    })).apply(module);
  }
}
