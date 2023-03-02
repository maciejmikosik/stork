package com.mikosik.stork.compile;

import static com.mikosik.stork.model.Module.module;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Module;

public class Link {
  public static Module link(Chain<Module> modules) {
    return module(modules.flatMap(module -> module.definitions));
  }
}
