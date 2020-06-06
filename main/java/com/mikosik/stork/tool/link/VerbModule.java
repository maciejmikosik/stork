package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.common.Translate.asStorkInteger;
import static com.mikosik.stork.tool.common.Verbs.opInt;
import static com.mikosik.stork.tool.common.Verbs.opIntInt;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class VerbModule {
  public static Module verbModule() {
    Chain<Definition> definitions = chainOf(
        definition("add", opIntInt("addIntInt",
            (numberA, numberB) -> asStorkInteger(numberA.add(numberB)))),
        definition("negate", opInt("negateInt",
            number -> asStorkInteger(number.negate()))),
        definition("equal", opIntInt("equalIntInt",
            (numberA, numberB) -> asStorkBoolean(numberA.equals(numberB)))),
        definition("moreThan", opIntInt("moreThanIntInt",
            (numberA, numberB) -> asStorkBoolean(numberB.compareTo(numberA) > 0))));
    return module(definitions);
  }
}
