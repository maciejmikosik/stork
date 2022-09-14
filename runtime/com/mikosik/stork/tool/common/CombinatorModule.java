package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.tool.common.Constants.B;
import static com.mikosik.stork.tool.common.Constants.C;
import static com.mikosik.stork.tool.common.Constants.I;
import static com.mikosik.stork.tool.common.Constants.K;
import static com.mikosik.stork.tool.common.Constants.S;
import static com.mikosik.stork.tool.common.Constants.Y;
import static com.mikosik.stork.tool.common.Instructions.instruction1;
import static com.mikosik.stork.tool.common.Instructions.instruction2;
import static com.mikosik.stork.tool.common.Instructions.instruction3;
import static com.mikosik.stork.tool.common.Instructions.name;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;

public class CombinatorModule {
  public static Module combinatorModule() {
    return module(chainOf(
        /** I(x) = x */
        define(I, instruction1(x -> x)),

        /** Y(x) = x(Y(x)) */
        define(Y, instruction1(x -> ap(x, ap(Y, x)))),

        /** K(x)(y) = x */
        define(K, instruction2(x -> y -> x)),

        /** S(x)(y)(z) = x(z)(y(z)) */
        define(S, instruction3(x -> y -> z -> ap(ap(x, z), ap(y, z)))),

        /** C(x)(y)(z) = x(z)(y) */
        define(C, instruction3(x -> y -> z -> ap(ap(x, z), y))),

        /** B(x)(y)(z) = x(y(z)) */
        define(B, instruction3(x -> y -> z -> ap(x, ap(y, z))))));
  }

  private static Expression ap(Expression function, Expression argument) {
    return application(function, argument);
  }

  public static Definition define(Identifier identifier, Instruction body) {
    return definition(identifier, name(identifier, body));
  }
}
