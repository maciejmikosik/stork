package com.mikosik.stork.tool.run;

import static com.mikosik.stork.data.model.Switch.switchOn;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;

public class ModuleRunner implements Runner {
  private final Map<String, Expression> table;

  private ModuleRunner(Map<String, Expression> table) {
    this.table = table;
  }

  public static Runner runner(Module module) {
    Map<String, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.name, definition.expression);
    }
    return new ModuleRunner(table);
  }

  public Expression run(Expression expression) {
    return switchOn(expression)
        .ifVariable(variable -> table.get(variable.name))
        .elseFail();
  }
}
