package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainFrom;
import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.data.model.Module.module;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class OverridingLinker implements Linker {
  private final Module overridingModule;
  private final Linker linker;

  private OverridingLinker(Module overridingModule, Linker linker) {
    this.overridingModule = overridingModule;
    this.linker = linker;
  }

  public static Linker overriding(Module overridingModule, Linker linker) {
    return new OverridingLinker(overridingModule, linker);
  }

  public Module link(Chain<Module> modules) {
    Map<String, Definition> overridingMap = stream(overridingModule.definitions)
        .collect(toMap(
            definition -> definition.name,
            definition -> definition));

    Module linkedModule = linker.link(modules);

    List<Definition> definitions = stream(linkedModule.definitions)
        .map(definition -> overridingMap.getOrDefault(definition.name, definition))
        .collect(toList());
    return module(chainFrom(definitions));
  }
}
