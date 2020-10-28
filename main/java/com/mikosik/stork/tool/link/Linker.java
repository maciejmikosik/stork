package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.tool.link.Repository.repository;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class Linker {
  public static Module coreModule() {
    Chain<String> moduleNames = chainOf(
        "opcode.stork",
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork");
    Repository repository = repository();
    return link(moduleNames.map(repository::module));
  }

  public static Module link(Chain<Module> modules) {
    return noCollisions(join(modules));
  }

  private static Module join(Chain<Module> modules) {
    Chain<Definition> definitions = modules
        .flatMap(module -> module.definitions);
    return module(definitions);
  }

  // TODO test collisions handling
  private static Module noCollisions(Module module) {
    Set<String> keys = new HashSet<>();
    module.definitions.stream()
        .forEach(definition -> {
          check(!keys.contains(definition.variable.name));
          keys.add(definition.variable.name);
        });
    return module;
  }
}
