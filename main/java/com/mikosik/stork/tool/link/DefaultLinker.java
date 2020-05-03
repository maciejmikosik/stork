package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainFrom;
import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Module.module;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class DefaultLinker implements Linker {
  private DefaultLinker() {}

  public static Linker defaultLinker() {
    return new DefaultLinker();
  }

  public Module link(Chain<Module> modules) {
    Set<String> keys = new HashSet<>();
    List<Definition> definitions = stream(modules)
        .flatMap(module -> stream(module.definitions))
        .map(definition -> ensureUnique(keys, definition))
        .collect(toList());
    return module(chainFrom(definitions));
  }

  // TODO move collision handling to separate implementation
  // TODO test collisions handling
  private Definition ensureUnique(Set<String> keys, Definition definition) {
    check(!keys.contains(definition.name));
    keys.add(definition.name);
    return definition;
  }
}
