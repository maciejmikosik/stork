package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Check.check;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public class UniqueLinker implements Linker {
  private final Linker linker;

  private UniqueLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker unique(Linker linker) {
    return new UniqueLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return checkUnique(linker.link(modules));
  }

  // TODO test collisions handling
  private static Module checkUnique(Module module) {
    Set<String> keys = new HashSet<>();
    // TODO throw dedicated exception
    module.definitions.stream()
        .forEach(definition -> {
          check(!keys.contains(definition.variable.name));
          keys.add(definition.variable.name);
        });
    return module;
  }
}
