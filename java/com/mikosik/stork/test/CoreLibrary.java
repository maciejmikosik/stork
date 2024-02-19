package com.mikosik.stork.test;

import static com.mikosik.stork.build.Stars.buildCoreLibrary;
import static com.mikosik.stork.common.io.InputOutput.path;

import com.mikosik.stork.model.Module;

public class CoreLibrary {
  public static final Module CORE_LIBRARY = buildCoreLibrary(path("core_library"));
}
