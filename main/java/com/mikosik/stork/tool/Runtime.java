package com.mikosik.stork.tool;

import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Expression;

public class Runtime {
  private final Library library;

  private Runtime(Library library) {
    this.library = library;
  }

  public static Runtime runtime(Library library) {
    return new Runtime(library);
  }

  public Expression find(String name) {
    return library.get(name);
  }
}
