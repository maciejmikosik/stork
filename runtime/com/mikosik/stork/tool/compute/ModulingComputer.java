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
  private final Map<String, Expression> table;
  private final Computer computer;

  private ModulingComputer(Map<String, Expression> table, Computer computer) {
    this.table = table;
    this.computer = computer;
  }

  public static Computer moduling(Module module, Computer computer) {
    Map<String, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.identifier.name, definition.body);
    }
    return new ModulingComputer(table, computer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Identifier) {
      Identifier identifier = (Identifier) computation.expression;
      if (table.containsKey(identifier.name)) {
        return computation(
            table.get(identifier.name),
            computation.stack);
      }
    }
    return computer.compute(computation);
  }
}
