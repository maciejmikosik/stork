package com.mikosik.stork.lib;

import static com.mikosik.stork.common.InputOutput.readResource;
import static com.mikosik.stork.tool.Default.compileModule;

import com.mikosik.stork.data.model.Module;

public class Modules {
  public static Module module(String name) {
    return compileModule(readResource(Modules.class, name));
  }
}
