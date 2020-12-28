package com.mikosik.stork.front.core;

import static com.mikosik.stork.common.Input.resource;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Module;

public class Repository {
  private Repository() {}

  public static Repository repository() {
    return new Repository();
  }

  public Module module(String fileName) {
    try (Input input = resource(Repository.class, fileName).buffered()) {
      return defaultCompiler().compile(input);
    }
  }
}
