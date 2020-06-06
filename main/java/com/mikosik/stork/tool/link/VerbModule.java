package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Noun.noun;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.common.Verbs.opInt;
import static com.mikosik.stork.tool.common.Verbs.opIntInt;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;

public class VerbModule {
  public static Module verbModule() {
    Chain<Definition> definitions = chainOf(
        definition("add", opIntInt("addIntInt",
            (numberA, numberB) -> noun(numberA.add(numberB)))),
        definition("negate", opInt("negateInt",
            number -> noun(number.negate()))),
        definition("equal", opIntInt("equalIntInt",
            (numberA, numberB) -> variable("" + numberA.equals(numberB)))),
        definition("moreThan", opIntInt("moreThanIntInt",
            (numberA, numberB) -> variable("" + (numberB.compareTo(numberA) > 0)))));
    return module(definitions);
  }
}
