package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.tool.LinkableVerbs.addIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.equalIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.moreThanIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.negateInteger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;

public class DefaultLinker implements Linker {
  private DefaultLinker() {}

  public static Linker defaultLinker() {
    return new DefaultLinker();
  }

  public Module link(Chain<Module> modules) {
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

    Chain<Definition> result = empty();
    for (Entry<String, Expression> entry : table.entrySet()) {
      result = add(definition(entry.getKey(), entry.getValue()), result);
    }

    return module(result);
  }
}
