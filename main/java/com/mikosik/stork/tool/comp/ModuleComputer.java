package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Computation;

public class ModuleComputer implements Computer {
  private final Map<String, Expression> table;

  private ModuleComputer(Map<String, Expression> table) {
    this.table = table;
  }

  public static Computer computer(Module module) {
    Map<String, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.name, definition.expression);
    }
    return new ModuleComputer(table);
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Variable
        ? computation(
            table.get(((Variable) computation.expression).name),
            computation.stack)
        : fail(format("cannot handle expression '%s' of type %s",
            computation.expression,
            computation.expression.getClass().getName()));
  }
}
