package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.LinkableVerbs.addIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.equalIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.moreThanIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.negateInteger;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.run.Runner;

public class Linker {
  public static Runner link(Chain<Module> modules) {
    Map<String, Expression> table = new HashMap<>();

    for (Module module : modules) {
      for (Definition definition : module.definitions) {
        // TODO check collisions
        table.put(definition.name, definition.expression);
      }
    }

    table.replace("add", addIntegerInteger());
    table.replace("negate", negateInteger());
    table.replace("equal", equalIntegerInteger());
    table.replace("moreThan", moreThanIntegerInteger());

    return expression -> switchOn(expression)
        .ifVariable(variable -> table.get(variable.name))
        .elseFail();
  }
}
