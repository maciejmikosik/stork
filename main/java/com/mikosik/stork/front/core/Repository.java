package com.mikosik.stork.front.core;

import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;

import java.nio.file.Paths;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.model.Module;

public class Repository {
  private Repository() {}

  public static Repository repository() {
    return new Repository();
  }

  public Module module(String fileName) {
    try (Input input = input(Paths.get("main/stork", fileName)).buffered()) {
      return defaultCompiler().compile(input);
    }
  }
}
