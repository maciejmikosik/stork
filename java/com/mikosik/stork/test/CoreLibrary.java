package com.mikosik.stork.test;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.compile.Compiler.compileCoreLibrary;

import com.mikosik.stork.model.Module;

public class CoreLibrary {
  public static final Module CORE_LIBRARY = compileCoreLibrary(project().coreLibraryDirectory);
}
