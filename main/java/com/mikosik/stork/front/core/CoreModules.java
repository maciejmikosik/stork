package com.mikosik.stork.front.core;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.front.core.Repository.repository;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Module;

public class CoreModules {
  public static Chain<Module> coreModules() {
    Chain<String> moduleNames = chainOf(
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork");
    Repository repository = repository();
    return moduleNames.map(repository::module);
  }
}
