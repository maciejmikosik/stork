package com.mikosik.stork.compile;

import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifIdentifier;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class CheckUndefined {
  // TODO throw dedicated exception
  public static void checkUndefined(Module module) {
    onEachDefinition(onBody(deep(ifVariable(variable -> {
      throw new RuntimeException(variable.name);
    })))).apply(module);
    Set<Identifier> defined = module.definitions.stream()
        .map(definition -> definition.identifier)
        .collect(toSet());
    onEachDefinition(onBody(deep(ifIdentifier(identifier -> {
      if (!defined.contains(identifier)) {
        throw new RuntimeException(identifier.name());
      } else {
        return identifier;
      }
    })))).apply(module);
  }
}
