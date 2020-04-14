package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.chain;
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
    Chain<Mod<String, Expression>> mods = chain();
    for (Library library : libraries) {
      for (Definition definition : library.definitions) {
        // TODO check collisions
        mods = mods.add(put(definition.name, definition.expression));
      }
    }
    mods = mods
        .add(replaceIfPresent("add", addIntegerInteger()))
        .add(replaceIfPresent("negate", negateInteger()))
        .add(replaceIfPresent("equal", equalIntegerInteger()))
        .add(replaceIfPresent("moreThan", moreThanIntegerInteger()));

    return binary(table(mods.reverse()));
  }
}
