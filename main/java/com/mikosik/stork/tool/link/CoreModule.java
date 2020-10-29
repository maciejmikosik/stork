package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.tool.link.Linker.link;
import static com.mikosik.stork.tool.link.Repository.repository;

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
    return link(moduleNames.map(repository::module));
  }
}
