package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainFrom;
import static com.mikosik.stork.common.Chains.stream;
import static com.mikosik.stork.data.model.Module.module;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class DefaultLinker implements Linker {
  private DefaultLinker() {}

  public static Linker defaultLinker() {
    return new DefaultLinker();
  }

  public Module link(Chain<Module> modules) {
    List<Definition> definitions = stream(modules)
        .flatMap(module -> stream(module.definitions))
        .collect(toList());
    return module(chainFrom(definitions));
  }
}
