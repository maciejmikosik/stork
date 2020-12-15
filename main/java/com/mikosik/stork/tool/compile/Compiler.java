package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Module;

public class Compiler {
  private Compiler() {}

  public static Compiler compiler() {
    return new Compiler();
  }

  public Module compile(Input input) {
    return modelModule(parse(input));
  }
}
