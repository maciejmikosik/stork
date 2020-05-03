package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Noun.noun;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.Verbs.verb;

import java.math.BigInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Noun;

public class VerbModule {
  public static Module verbModule() {
    Chain<Definition> definitions = chainOf(
        definition("add", verb("add", (argumentA, argumentB) -> {
          BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
          BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
          return noun(numberA.add(numberB));
        })),
        definition("negate", verb("negate", (argument) -> {
          BigInteger number = (BigInteger) ((Noun) argument).object;
          return noun(number.negate());
        })),
        definition("equal", verb("equal", (argumentA, argumentB) -> {
          BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
          BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
          return variable("" + numberA.equals(numberB));
        })),
        definition("moreThan", verb("moreThan", (argumentA, argumentB) -> {
          BigInteger numberA = (BigInteger) ((Noun) argumentA).object;
          BigInteger numberB = (BigInteger) ((Noun) argumentB).object;
          return variable("" + (numberB.compareTo(numberA) > 0));
        })));
    return module(definitions);
  }
}
