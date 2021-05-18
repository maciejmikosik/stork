package com.mikosik.stork.front.program;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.tool.common.InnateBuilder.innate;

import java.math.BigInteger;

import com.mikosik.stork.model.Module;

public class InnateMathModule {
  public static Module innateMathModule() {
    return module(chainOf(
        innate()
            .name("$NEGATE")
            .logicIntInt(BigInteger::negate)
            .defineAs("stork.innate.innateNegate"),
        innate()
            .name("$ADD")
            .logicIntIntInt(BigInteger::add)
            .defineAs("stork.innate.innateAdd"),
        innate()
            .name("$EQUAL")
            .logicIntIntBool(BigInteger::equals)
            .defineAs("stork.innate.innateEqual"),
        innate()
            .name("$MORE_THAN")
            .logicIntIntBool((x, y) -> x.compareTo(y) < 0)
            .defineAs("stork.innate.innateMoreThan")));
  }
}
