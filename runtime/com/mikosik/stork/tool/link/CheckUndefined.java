package com.mikosik.stork.tool.link;

import java.util.Set;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class CheckUndefined {
  // TODO throw dedicated exception
  public static void checkUndefined(Module module) {
    Set<String> defined = module.definitions
        .map(definition -> definition.identifier.name)
        .toHashSet();
    new Traverser() {
      protected Expression traverse(Variable variable) {
        throw new RuntimeException(variable.name);
      }

      protected Identifier traverse(Identifier identifier) {
        if (!defined.contains(identifier.name)) {
          throw new RuntimeException(identifier.name);
        }
        return super.traverse(identifier);
      }
    }.traverse(module);
  }
}
