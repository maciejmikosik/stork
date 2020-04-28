package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.addAll;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.table.Put.put;
import static com.mikosik.stork.common.table.ReplaceIfPresent.replaceIfPresent;
import static com.mikosik.stork.common.table.Table.table;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.tool.Binary.binary;
import static com.mikosik.stork.tool.LinkableVerbs.addIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.equalIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.moreThanIntegerInteger;
import static com.mikosik.stork.tool.LinkableVerbs.negateInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.table.Mod;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.run.Runner;

public class Linker {
  public static Runner link(Chain<Module> modules) {
    Binary binary = linkBinary(modules);
    return expression -> switchOn(expression)
        .ifVariable(variable -> binary.table.get(variable.name))
        .elseFail();
  }

  private static Binary linkBinary(Chain<Module> modules) {
    Chain<Mod<String, Expression>> moduleDefinitions = definitionsFrom(modules);
    Chain<Mod<String, Expression>> linkedDefinitions = chainOf(
        replaceIfPresent("add", addIntegerInteger()),
        replaceIfPresent("negate", negateInteger()),
        replaceIfPresent("equal", equalIntegerInteger()),
        replaceIfPresent("moreThan", moreThanIntegerInteger()));
    Chain<Mod<String, Expression>> definitions = addAll(moduleDefinitions, linkedDefinitions);
    return binary(table(definitions));
  }

  private static Chain<Mod<String, Expression>> definitionsFrom(Chain<Module> modules) {
    Chain<Mod<String, Expression>> mods = empty();
    for (Module module : modules) {
      for (Definition definition : module.definitions) {
        // TODO check collisions
        mods = add(put(definition.name, definition.expression), mods);
      }
    }
    return mods;
  }
}
