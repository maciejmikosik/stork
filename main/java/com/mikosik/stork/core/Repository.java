package com.mikosik.stork.core;

import static com.mikosik.stork.common.Input.resource;
import static com.mikosik.stork.tool.compile.Compiler.compiler;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Module;

public class Repository {
  private Repository() {}

  public static Repository repository() {
    return new Repository();
  }

  public Module module(String fileName) {
    try (Input input = resource(Repository.class, fileName).buffered()) {
      return compiler().compile(input);
    }
  }
}
