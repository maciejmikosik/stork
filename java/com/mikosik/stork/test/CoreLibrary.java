package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.compile.Compiler.compileCoreLibrary;

import com.mikosik.stork.model.Module;

public class CoreLibrary {
  public static final Module CORE_LIBRARY = compileCoreLibrary(path("core_library"));
}
