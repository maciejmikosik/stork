package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;

public class ModulingComputer implements Computer {
  private final Map<Identifier, Expression> table;

  private ModulingComputer(Map<Identifier, Expression> table) {
    this.table = table;
  }

  public static Computer modulingComputer(Module module) {
    Map<Identifier, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.identifier, definition.body);
    }
    return new ModulingComputer(table);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Identifier identifier
        && table.containsKey(identifier)
            ? computation(
                table.get(identifier),
                computation.stack)
            : computation;
  }
}
