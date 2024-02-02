package com.mikosik.stork.debug;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.NamedInstruction.name;
import static com.mikosik.stork.model.change.Changes.onEachDefinition;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.NamedInstruction;

public class InjectNames {
  public static Module injectNames(Module module) {
    return onEachDefinition(InjectNames::injectName)
        .apply(module);
  }

  private static Definition injectName(Definition definition) {
    var identifier = definition.identifier;
    var body = definition.body;

    if (body instanceof EagerInstruction eager) {
      return definition(
          identifier,
          eager(name(identifier, eager.instruction)));
    } else if (body instanceof NamedInstruction) {
      return definition;
    } else if (body instanceof Instruction instruction) {
      return definition(
          identifier,
          name(identifier, instruction));
    } else {
      return definition;
    }
  }
}
