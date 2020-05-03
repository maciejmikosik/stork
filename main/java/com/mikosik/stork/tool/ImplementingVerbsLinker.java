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

public class ImplementingVerbsLinker implements Linker {
  private final Linker linker;

  private ImplementingVerbsLinker(Linker linker) {
    this.linker = linker;
  }

  public static Linker implementingVerbs(Linker linker) {
    return new ImplementingVerbsLinker(linker);
  }

  public Module link(Chain<Module> modules) {
    Module module = linker.link(modules);

    Map<String, Expression> table = new HashMap<>();
    for (Definition definition : module.definitions) {
      table.put(definition.name, definition.expression);
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
