package com.mikosik.stork.tool.link;

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
    return onEachDefinition(InjectNames::intoInstruction).apply(module);
  }

  private static Definition intoInstruction(Definition definition) {
    if (definition.body instanceof EagerInstruction eager) {
      return definition(
          definition.identifier,
          eager(name(definition.identifier, eager.instruction)));
    } else if (definition.body instanceof NamedInstruction instruction) {
      return definition;
    } else if (definition.body instanceof Instruction instruction) {
      return definition(
          definition.identifier,
          name(definition.identifier, instruction));
    } else {
      return definition;
    }
  }
}
