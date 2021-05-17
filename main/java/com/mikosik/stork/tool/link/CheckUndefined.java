package com.mikosik.stork.tool.link;

import java.util.Set;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class CheckUndefined {
  // TODO test coherence
  public static void checkUndefined(Module module) {
    Set<String> defined = module.definitions
        .map(definition -> definition.variable.name)
        .toHashSet();
    new Traverser() {
      protected Expression traverse(Variable variable) {
        if (!defined.contains(variable.name)) {
          // TODO throw dedicated exception
          throw new RuntimeException(variable.name);
        }
        return super.traverse(variable);
      }
    }.traverse(module);
  }
}
