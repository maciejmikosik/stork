package com.mikosik.stork.tool.link;

import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class UndefinedVariablesDetector implements Weaver {
  private UndefinedVariablesDetector() {}

  public static Weaver undefinedVariablesDetector() {
    return new UndefinedVariablesDetector();
  }

  // TODO test coherence
  public Module weave(Module module) {
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
