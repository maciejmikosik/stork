package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.table.Put.put;
import static com.mikosik.stork.common.table.Table.table;
import static com.mikosik.stork.tool.Binary.binary;

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
    return binary(table(mods));
  }
}
