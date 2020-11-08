package com.mikosik.stork.tool.link;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public class BuildingLinker implements Linker {
  private final Linker linker;

  private BuildingLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker building(Linker linker) {
    return new BuildingLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return linker.link(modules.map(BuildingLinker::build));
  }

  private static Module build(Module module) {
    // TODO stub
    return module;
  }
}
