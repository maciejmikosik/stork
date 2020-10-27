package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.data.model.comp.Computation.computation;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;

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
      table.put(definition.variable.name, definition.expression);
    }
    return new ModulingComputer(table, computer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      if (table.containsKey(variable.name)) {
        return computation(
            table.get(variable.name),
            computation.stack);
      }
    }
    return computer.compute(computation);
  }
}
