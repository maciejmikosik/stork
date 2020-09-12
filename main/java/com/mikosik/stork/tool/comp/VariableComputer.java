package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;

public class VariableComputer implements Computer {
  private final Map<String, Expression> table;
  private final Computer nextComputer;

  private VariableComputer(Map<String, Expression> table, Computer nextComputer) {
    this.table = table;
    this.nextComputer = nextComputer;
  }

  public static Computer variable(Module module, Computer nextComputer) {
    Map<String, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.name, definition.expression);
    }
    return new VariableComputer(table, nextComputer);
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
    return nextComputer.compute(computation);
  }
}
