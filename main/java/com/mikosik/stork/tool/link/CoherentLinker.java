package com.mikosik.stork.tool.link;

import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class CoherentLinker implements Linker {
  private final Linker linker;

  private CoherentLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker coherent(Linker linker) {
    return new CoherentLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    return check(linker.link(modules));
  }

  // TODO test coherence
  private static Module check(Module module) {
    Set<String> defined = module.definitions.stream()
        .map(definition -> definition.variable.name)
        .collect(toCollection(HashSet::new));
    new Traverser() {
      protected Expression traverse(Variable variable) {
        if (!defined.contains(variable.name)) {
          // TODO throw dedicated exception
          throw new RuntimeException(variable.name);
        }
        return super.traverse(variable);
      }
    }.traverse(module);
    return module;
  }
}
