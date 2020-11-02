package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.tool.link.OpcodeModule.opcodeModule;
import static com.mikosik.stork.tool.link.Repository.repository;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

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
        .unique()
        .coherent();
    Chain<Module> modules = moduleNames.map(repository::module)
        .add(opcodeModule());
    return linker.link(modules);
  }

}
