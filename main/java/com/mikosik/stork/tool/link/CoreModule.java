package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.tool.link.Repository.repository;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;

public class CoreModule {
  public static Module coreModule() {
    Chain<String> moduleNames = chainOf(
        "opcode.stork",
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork");
    Repository repository = repository();
    Linker linker = linker().unique();
    return linker.link(moduleNames.map(repository::module));
  }
}
