package com.mikosik.stork.build.link;

import static com.mikosik.stork.model.Module.module;

import java.util.Arrays;
import java.util.List;

import com.mikosik.stork.model.Module;

public class Modules {
  public static Module join(List<Module> modules) {
    return module(modules.stream()
        .flatMap(module -> module.definitions.stream())
        .toList());
  }

  public static Module join(Module... modules) {
    return join(Arrays.asList(modules));
  }
}
