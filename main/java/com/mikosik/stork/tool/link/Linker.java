package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainFrom;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.tool.link.Repository.repository;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
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
    List<Module> modules = stream(moduleNames)
        .map(repository::module)
        .collect(toList());
    return link(chainFrom(modules));
  }

  public static Module link(Chain<Module> modules) {
    return noCollisions(join(modules));
  }

  private static Module join(Chain<Module> modules) {
    List<Definition> definitions = stream(modules)
        .flatMap(module -> stream(module.definitions))
        .collect(toList());
    return module(chainFrom(definitions));
  }

  // TODO test collisions handling
  private static Module noCollisions(Module module) {
    Set<String> keys = new HashSet<>();
    stream(module.definitions)
        .forEach(definition -> {
          check(!keys.contains(definition.variable.name));
          keys.add(definition.variable.name);
        });
    return module;
  }
}
