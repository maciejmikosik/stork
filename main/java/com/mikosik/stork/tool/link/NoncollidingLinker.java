package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.common.Check.check;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

// TODO test collisions handling
public class NoncollidingLinker implements Linker {
  private final Linker linker;

  private NoncollidingLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker noncolliding(Linker linker) {
    return new NoncollidingLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    Module module = linker.link(modules);
    Set<String> keys = new HashSet<>();
    stream(module.definitions)
        .forEach(definition -> {
          check(!keys.contains(definition.variable.name));
          keys.add(definition.variable.name);
        });
    return linker.link(modules);
  }
}
