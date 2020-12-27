package com.mikosik.stork.core;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.core.JavaModule.javaModule;
import static com.mikosik.stork.core.Repository.repository;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.link.Linker;

public class CoreModule {
  public static Module coreModule() {
    Chain<String> moduleNames = chainOf(
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork");
    Repository repository = repository();
    Linker linker = linker()
        .building()
        .opcoding()
        .unique()
        .coherent();
    Chain<Module> modules = moduleNames.map(repository::module)
        .add(javaModule());
    return linker.link(modules);
  }

}
