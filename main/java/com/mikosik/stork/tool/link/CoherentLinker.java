package com.mikosik.stork.tool.link;

import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Variable;

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
    module.definitions.stream()
        .forEach(definition -> check(defined, definition.expression));
    return module;
  }

  private static void check(Set<String> defined, Expression expression) {
    if (expression instanceof Application) {
      Application application = (Application) expression;
      check(defined, application.function);
      check(defined, application.argument);
    } else if (expression instanceof Lambda) {
      Lambda lambda = (Lambda) expression;
      check(defined, lambda.body);
    } else if (expression instanceof Variable) {
      Variable variable = (Variable) expression;
      if (!defined.contains(variable.name)) {
        // TODO throw dedicated exception
        throw new RuntimeException(variable.name);
      }
    }
  }
}
