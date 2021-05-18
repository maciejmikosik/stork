package com.mikosik.stork.front.core;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.tool.common.InnateBuilder.innate;

import java.math.BigInteger;

import com.mikosik.stork.model.Module;

public class JavaModule {
  public static Module javaModule() {
    return module(chainOf(
        innate()
            .name("$javaNegate")
            .logicIntInt(BigInteger::negate)
            .defineAs("stork.java.math.BigInteger.javaNegate"),
        innate()
            .name("$javaAdd")
            .logicIntIntInt(BigInteger::add)
            .defineAs("stork.java.math.BigInteger.javaAdd"),
        innate()
            .name("$javaAdd")
            .logicIntIntBool(BigInteger::equals)
            .defineAs("stork.java.math.BigInteger.javaEquals"),
        innate()
            .name("$javaCompareTo")
            .logicIntIntInt((arg, thiz) -> BigInteger.valueOf(thiz.compareTo(arg)))
            .defineAs("stork.java.math.BigInteger.javaCompareTo")));
  }
}
