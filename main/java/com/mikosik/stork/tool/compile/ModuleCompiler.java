package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.data.model.Module.module;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class ModuleCompiler implements Compiler<Module> {
  public Compiler<Definition> definition;
  public Compiler<Void> whitespace;

  public Module compile(Input input) {
    Chain<Definition> definitions = empty();
    while (input.peek() != -1) {
      whitespace.compile(input);
      definitions = definitions.add(definition.compile(input));
      whitespace.compile(input);
    }
    return module(definitions.reverse());
  }
}
