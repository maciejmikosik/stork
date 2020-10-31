package com.mikosik.stork.tool.link;

import static com.mikosik.stork.data.model.Module.module;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class JoiningLinker implements Linker {
  private JoiningLinker() {}

  public static Linker joiningLinker() {
    return new JoiningLinker();
  }

  public Module link(Chain<Module> modules) {
    Chain<Definition> definitions = modules
        .flatMap(module -> module.definitions);
    return module(definitions);
  }
}
