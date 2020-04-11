package com.mikosik.stork.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;

public class Runtime {
  private final Map<String, Expression> definitions;

  private Runtime(Map<String, Expression> definitions) {
    this.definitions = definitions;
  }

  public static Runtime runtime(Chain<Library> libraries) {
    Map<String, Expression> definitions = new HashMap<>();
    for (Library library : libraries) {
      for (Definition definition : library.definitions) {
        // TODO check collisions
        definitions.put(definition.name, definition.expression);
      }
    }
    return new Runtime(definitions);
  }

  public Expression find(String name) {
    return Optional.ofNullable(definitions.get(name))
        .orElseThrow(() -> new RuntimeException(name));
  }
}
