package com.mikosik.stork.tool.common;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Instruction.instruction;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.NamedInstruction.name;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;

public class CombinatoryModule {
  public static final Identifier I = identifier("stork.function.native.I");
  public static final Identifier Y = identifier("stork.function.native.Y");
  public static final Identifier K = identifier("stork.function.native.K");
  public static final Identifier S = identifier("stork.function.native.S");
  public static final Identifier C = identifier("stork.function.native.C");
  public static final Identifier B = identifier("stork.function.native.B");

  public static Module combinatoryModule() {
    return module(chainOf(
        /** I(x) = x */
        define(I, instruction(x -> x)),

        /** Y(x) = x(Y(x)) */
        define(Y, instruction(x -> ap(x, ap(Y, x)))),

        /** K(x)(y) = x */
        define(K, instruction((x, y) -> x)),

        /** S(x)(y)(z) = x(z)(y(z)) */
        define(S, instruction((x, y, z) -> ap(ap(x, z), ap(y, z)))),

        /** C(x)(y)(z) = x(z)(y) */
        define(C, instruction((x, y, z) -> ap(ap(x, z), y))),

        /** B(x)(y)(z) = x(y(z)) */
        define(B, instruction((x, y, z) -> ap(x, ap(y, z))))));
  }

  private static Definition define(Identifier identifier, Instruction instruction) {
    return definition(identifier.name, name(identifier.name, instruction));
  }

  private static Expression ap(Expression function, Expression argument) {
    return application(function, argument);
  }
}
