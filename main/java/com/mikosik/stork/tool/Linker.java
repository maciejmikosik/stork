package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.addAll;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.table.Put.put;
import static com.mikosik.stork.common.table.ReplaceIfPresent.replaceIfPresent;
import static com.mikosik.stork.common.table.Table.table;
import static com.mikosik.stork.tool.Binary.binary;
import static com.mikosik.stork.tool.LinkedFunctions.addIntegerInteger;
import static com.mikosik.stork.tool.LinkedFunctions.equalIntegerInteger;
import static com.mikosik.stork.tool.LinkedFunctions.moreThanIntegerInteger;
import static com.mikosik.stork.tool.LinkedFunctions.negateInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.table.Mod;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;

public class Linker {
  public static Binary link(Chain<Library> libraries) {
    Chain<Mod<String, Expression>> libraryDefinitions = definitionsFrom(libraries);
    Chain<Mod<String, Expression>> linkedDefinitions = chainOf(
        replaceIfPresent("add", addIntegerInteger()),
        replaceIfPresent("negate", negateInteger()),
        replaceIfPresent("equal", equalIntegerInteger()),
        replaceIfPresent("moreThan", moreThanIntegerInteger()));
    Chain<Mod<String, Expression>> definitions = addAll(libraryDefinitions, linkedDefinitions);
    return binary(table(definitions));
  }

  private static Chain<Mod<String, Expression>> definitionsFrom(Chain<Library> libraries) {
    Chain<Mod<String, Expression>> mods = empty();
    for (Library library : libraries) {
      for (Definition definition : library.definitions) {
        // TODO check collisions
        mods = add(put(definition.name, definition.expression), mods);
      }
    }
    return mods;
  }
}
