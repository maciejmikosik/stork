package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Check.check;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.model.Module;

public class NameCollisionDetector implements Weaver {
  private NameCollisionDetector() {}

  public static NameCollisionDetector nameCollisionDetector() {
    return new NameCollisionDetector();
  }

  // TODO test collisions handling
  public Module weave(Module module) {
    Set<String> keys = new HashSet<>();
    // TODO throw dedicated exception
    module.definitions.stream()
        .forEach(definition -> {
          check(!keys.contains(definition.variable.name));
          keys.add(definition.variable.name);
        });
    return module;
  }
}
