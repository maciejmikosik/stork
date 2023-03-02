package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Check.check;

import java.util.HashSet;
import java.util.Set;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class CheckCollisions {
  // TODO test collisions handling
  public static void checkCollisions(Module module) {
    Set<Identifier> keys = new HashSet<>();
    // TODO throw dedicated exception
    module.definitions
        .forEach(definition -> {
          check(!keys.contains(definition.identifier));
          keys.add(definition.identifier);
        });
  }
}
